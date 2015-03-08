package com.market.trade.processor.utils;

import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final Logger LOGGER = Logger.getLogger(DateUtils.class);
    private static final String DATE_FORMAT = "dd-MMM-yy HH:mm:ss";

    /**
     * Converts a String formatted date into a timestamp
     *
     * @param date the date to be converted
     * @return a timestamp, corresponding to the incoming date
     */
    public static Timestamp getTimestampFromString(String date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date newDate = simpleDateFormat.parse(date);
            return new Timestamp(newDate.getTime());
        } catch (Exception ex) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unable to convert date string to timestamp: " + date + " * " + ex.getMessage());
            }
            return null;
        }
    }

}
