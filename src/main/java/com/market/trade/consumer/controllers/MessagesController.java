package com.market.trade.consumer.controllers;

import com.market.trade.common.models.MessageRequest;
import com.market.trade.processor.handlers.MessagesHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MessagesController {
    private static final Logger LOGGER = Logger.getLogger(MessagesController.class);
    private static final String ACKED_RESPONSE = "Ack";
    @Autowired
    private MessagesHandler messagesHandler;

    @RequestMapping(method = RequestMethod.POST, value = "/req")
    public @ResponseBody String handleRequestMessage(@RequestBody MessageRequest messageRequest) {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received the message request: " + messageRequest);
        }
        messagesHandler.putOnMessagesQueue(messageRequest);
        return ACKED_RESPONSE;
    }

}
