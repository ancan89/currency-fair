package com.market.trade.processor.controllers;

import com.market.trade.processor.handlers.StatisticsHandler;
import com.market.trade.processor.models.StatisticsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@EnableScheduling
public class StatisticsController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private StatisticsHandler statisticsHandler;

    @Scheduled(fixedDelay = 10000)
    public void sendStatistics() {
        messagingTemplate.convertAndSend("/topic/updates",
                statisticsHandler.getStatistics());
    }

    @MessageMapping("/statistics")
    @SendTo("/topic/updates")
    public StatisticsModel getStatistics() throws Exception {
        return statisticsHandler.getStatistics();
    }

}
