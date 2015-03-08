package com.market.trade.processor.dao;

import com.market.trade.common.models.MessageRequest;
import com.market.trade.processor.utils.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MessagesStoreDao {
    private static final Logger LOGGER = Logger.getLogger(MessagesStoreDao.class);
    private static final String SQL_INSERT_INTO_MESSAGESSTORE = "INSERT INTO \"MessagesStore\" (\"UserId\", \"CurrencyFrom\", \"CurrencyTo\", \"AmountSell\", \"AmountBuy\", \"Rate\", \"TimePlaced\", \"OriginatingCountry\") VALUES ( ? , ? , ? , ?, ? , ?, ? , ?)";
    private static final String SQL_SELECT_DISTINCT_COUNTRIES = "SELECT DISTINCT \"OriginatingCountry\" FROM \"MessagesStore\"; ";
    private static final String SQL_COUNT_MESSAGES_PER_COUNTRY = "SELECT COUNT(1) FROM \"MessagesStore\" WHERE \"OriginatingCountry\"=?";
    private static final String ORIG_COUNTRY_COLUMN_NAME = "OriginatingCountry";

    private JdbcTemplate jdbcTemplate;

    public MessagesStoreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Inserts a list of requests into the database
     *
     * @param messageRequests the transactions to be persisted
     */
    public void updateMessagesStore(List<MessageRequest> messageRequests) {
        try {
            jdbcTemplate.batchUpdate(SQL_INSERT_INTO_MESSAGESSTORE, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, messageRequests.get(i).getUserId());
                    ps.setString(2, messageRequests.get(i).getCurrencyFrom());
                    ps.setString(3, messageRequests.get(i).getCurrencyTo());
                    ps.setDouble(4, messageRequests.get(i).getAmountSell());
                    ps.setDouble(5, messageRequests.get(i).getAmountBuy());
                    ps.setDouble(6, messageRequests.get(i).getRate());
                    ps.setTimestamp(7, DateUtils.getTimestampFromString(messageRequests.get(i).getTimePlaced()));
                    ps.setString(8, messageRequests.get(i).getOriginatingCountry());
                }

                @Override
                public int getBatchSize() {
                    return messageRequests.size();
                }
            });
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * Computes statistics based on the messages that were persisted in the database
     *
     * @return a map between the name of the originating country of the transactions and the total number of them
     */
    public Map<String, AtomicInteger> getStatisticsFromMessageStore() {
        Map<String, AtomicInteger> statistics = new ConcurrentHashMap<String, AtomicInteger>();
        List<Map<String, Object>> allCountriesList;
        try {
            allCountriesList = jdbcTemplate.queryForList(SQL_SELECT_DISTINCT_COUNTRIES);
            if (allCountriesList != null) {
                for (Map<String, Object> country : allCountriesList) {
                    String countryCode = country.get(ORIG_COUNTRY_COLUMN_NAME).toString();
                    SqlRowSet result = jdbcTemplate.queryForRowSet(SQL_COUNT_MESSAGES_PER_COUNTRY, new String[]{countryCode});
                    if (result.first()) {
                        int messagesPerCountry = result.getInt(1);
                        statistics.put(countryCode, new AtomicInteger(messagesPerCountry));
                    }
                }
            }
        } catch (DataAccessException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return statistics;
    }
}
