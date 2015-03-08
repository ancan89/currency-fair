package com.market.trade.processor.threads;

import com.market.trade.common.models.MessageRequest;
import com.market.trade.processor.dao.MessagesStoreDao;
import com.market.trade.processor.handlers.StatisticsHandler;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ProcessMessageThread implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ProcessMessageThread.class);

    private BlockingQueue<MessageRequest> queue;
    private StatisticsHandler statisticsHandler;
    private MessagesStoreDao messagesStoreDao;
    private List<MessageRequest> buffer;
    private volatile boolean isRunning;

    public ProcessMessageThread(BlockingQueue<MessageRequest> queue, StatisticsHandler statisticsHandler, JdbcTemplate jdbcTemplate) {
        this.queue = queue;
        this.statisticsHandler = statisticsHandler;
        this.messagesStoreDao = new MessagesStoreDao(jdbcTemplate);
    }

    @Override
    public void run() {
        isRunning = true;
        MessageRequest request = null;
        while (isRunning) {
            try {
                request = queue.take();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
            addRequestToBuffer(request);
            statisticsHandler.updateStatistics(request);
        }
    }

    /**
     * Adds a request to a buffer {@link #buffer} that will be flushed into the database once it gets to 50 elements
     *
     * @param messageRequest the message to be added to the buffer
     */
    private void addRequestToBuffer(MessageRequest messageRequest) {
        if (messageRequest != null) {
            getBuffer().add(messageRequest);
        }
        if (getBuffer().size() >= 50) {
            messagesStoreDao.updateMessagesStore(getBuffer());
            getBuffer().clear();
        }
    }

    /**
     * Getter method for the messages {@link #buffer}
     *
     * @return the {@link #buffer} or a new instance of it, if the buffer was not initialized
     */
    private List<MessageRequest> getBuffer() {
        if (buffer == null) {
            buffer = new ArrayList<>();
        }
        return buffer;
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
        if (!isRunning) {
            LOGGER.info("Flushing the buffer...");
            messagesStoreDao.updateMessagesStore(getBuffer());
        }
    }

}
