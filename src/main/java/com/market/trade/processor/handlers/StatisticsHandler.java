package com.market.trade.processor.handlers;

import com.market.trade.common.models.MessageRequest;
import com.market.trade.processor.dao.MessagesStoreDao;
import com.market.trade.processor.models.StatisticsModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@DependsOn(value = "jdbcTemplate")
public class StatisticsHandler {
    private static final Logger LOGGER = Logger.getLogger(StatisticsHandler.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private Map<String, AtomicInteger> countryToTotalTransactions;

    /**
     * Updates the statistics holder, {@link #countryToTotalTransactions}, in real-time, once a new transaction request has been received
     *
     * @param messageRequest the message to be added to the holder
     */
    public void updateStatistics(MessageRequest messageRequest) {
        if (messageRequest != null) {
            if (countryToTotalTransactions.containsKey(messageRequest.getOriginatingCountry())) {
                countryToTotalTransactions.get(messageRequest.getOriginatingCountry()).incrementAndGet();
            } else {
                countryToTotalTransactions.put(messageRequest.getOriginatingCountry(), new AtomicInteger(1));
            }
        }
    }

    /**
     * Creates a new object that is going to be pushed into the frontend
     *
     * @return the model to be pushed into the frontend
     */
    public StatisticsModel getStatistics() {
        Map<String, String> map = new HashMap<>();
        long totalTransactions = 0;
        for (Map.Entry<String, AtomicInteger> entry : countryToTotalTransactions.entrySet()) {
            totalTransactions += entry.getValue().intValue();
            map.put(entry.getKey(), String.valueOf(entry.getValue().intValue()));
        }
        return new StatisticsModel(String.valueOf(totalTransactions), map);
    }

    /**
     * Loads the statistics based on the data in the database
     */
    @PostConstruct
    private void loadStatisticsFromDb() {
        LOGGER.info("Loading statistics from the database...");
        MessagesStoreDao dbStatisticsLoader = new MessagesStoreDao(jdbcTemplate);
        countryToTotalTransactions = dbStatisticsLoader.getStatisticsFromMessageStore();
    }

}
