package com.market.trade.processor.dao;

import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MessagesStoreDaoTest {

    private static final String ORIG_COUNTRY_COLUMN_NAME = "OriginatingCountry";
    private static final String FRANCE = "FR";
    private static final String ROMANIA = "RO";
    private static final int EXPECTED_VALUE = 5;

    private JdbcTemplate jdbcTemplate;
    private MessagesStoreDao messagesStoreDao;

    @BeforeMethod
    public void setUp() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        messagesStoreDao = new MessagesStoreDao(jdbcTemplate);
    }

    @Test(dataProvider = "distinctCountriesDataProvider")
    public void getStatisticsFromMessageStoreTest(List<Map<String, Object>> distinctCountriesList) {
        Mockito.when(jdbcTemplate.queryForList(Mockito.isA(String.class))).thenReturn(distinctCountriesList);
        SqlRowSet sqlRowSet = Mockito.mock(SqlRowSet.class);
        Mockito.when(sqlRowSet.first()).thenReturn(true);
        Mockito.when(sqlRowSet.getInt(Mockito.eq(1))).thenReturn(EXPECTED_VALUE);
        Mockito.when(jdbcTemplate.queryForRowSet(Mockito.isA(String.class), Mockito.any(String[].class))).thenReturn(sqlRowSet).thenReturn(sqlRowSet);

        Map<String, AtomicInteger> result = messagesStoreDao.getStatisticsFromMessageStore();
        Assert.assertEquals(result.get(ROMANIA).intValue(), EXPECTED_VALUE);
        Assert.assertEquals(result.get(FRANCE).intValue(), EXPECTED_VALUE);
    }

    @DataProvider(name = "distinctCountriesDataProvider")
    public Object[][] dataProvider() {
        List<Map<String, Object>> distinctCountriesList = new ArrayList<>();
        Map<String, Object> france = new HashMap<>();
        france.put(ORIG_COUNTRY_COLUMN_NAME, FRANCE);
        Map<String, Object> romania = new HashMap<>();
        romania.put(ORIG_COUNTRY_COLUMN_NAME, ROMANIA);
        distinctCountriesList.add(france);
        distinctCountriesList.add(romania);
        return new Object[][]{{distinctCountriesList}};
    }
}
