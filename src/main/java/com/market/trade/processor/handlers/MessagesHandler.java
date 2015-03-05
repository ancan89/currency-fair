package com.market.trade.processor.handlers;

import com.market.trade.common.models.MessageRequest;
import com.market.trade.processor.threads.ProcessMessageThread;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagesHandler {
    private static final Logger LOGGER = Logger.getLogger(MessagesHandler.class);

    private BlockingQueue<MessageRequest> queue;
    private List<ProcessMessageThread> workers;

    public MessagesHandler(Integer queueSize, StatisticsHandler statisticsHandler, JdbcTemplate jdbcTemplate) {
        queue = new ArrayBlockingQueue<>(queueSize);
        workers = new ArrayList<>(20);
        ExecutorService taskExecutor = Executors.newFixedThreadPool(20);
        for(int i = 0; i < 20; i++) {
            ProcessMessageThread runnable = new ProcessMessageThread(queue, statisticsHandler, jdbcTemplate);
            workers.add(runnable);
            taskExecutor.execute(runnable);
        }
    }

    /**
     * Puts the transaction requests on a queue that will be processed by the {@link #workers}, enabling the messages consumer with a high speed of message consumption
     * @param messageRequest the transaction requests to be added to the queue
     */
    public void putOnMessagesQueue(MessageRequest messageRequest) {
        try {
            queue.put(messageRequest);
        } catch (InterruptedException e) {
            LOGGER.error("Could not put message on queue", e);
        }
    }

    /**
     * Stops all the running workers and flushes theirs buffers into the database
     */
    @PreDestroy
    public void flushBufferToDb() {
        for(ProcessMessageThread runnable : workers) {
            runnable.setIsRunning(false);
        }
    }
}
