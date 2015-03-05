package com.market.trade.processor.models;

import java.util.Map;

public class StatisticsModel {

    private String totalTransactions;
    private Map<String, String> countryToTotalTransactions;

    public StatisticsModel(String totalTransactions, Map<String, String> countryToTotalTransactions) {
        this.totalTransactions = totalTransactions;
        this.countryToTotalTransactions = countryToTotalTransactions;
    }

    public String getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(String totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Map<String, String> getCountryToTotalTransactions() {
        return countryToTotalTransactions;
    }

    public void setCountryToTotalTransactions(Map<String, String> countryToTotalTransactions) {
        this.countryToTotalTransactions = countryToTotalTransactions;
    }
}
