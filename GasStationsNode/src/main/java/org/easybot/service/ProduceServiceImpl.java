package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static org.easybot.RabbitQueue.ANSWER_EDITED_MESSAGE;
import static org.easybot.RabbitQueue.ANSWER_MESSAGE;
import static org.easybot.RabbitQueue.ERROR_MESSAGE;

@Component
@Slf4j
public class ProduceServiceImpl implements ProduceService{

    private final RabbitTemplate rabbitTemplate;

    public ProduceServiceImpl(RabbitTemplate rabbitTemplate)
    {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produceSimpleAnswer(SendMessage sendMessage)
    {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
        log.info("Message send from Gas Station Node to Rabbit");
    }

    @Override
    public void produceEditedAnswer(EditMessageText editMessageText)
    {
        rabbitTemplate.convertAndSend(ANSWER_EDITED_MESSAGE, editMessageText);
        log.info("Edited message send from Gas Station Node to Rabbit");
    }

    @Override
    public void produceErrorReport(SendMessage sendMessage)
    {
        rabbitTemplate.convertAndSend(ERROR_MESSAGE, sendMessage);
        log.info("Error report send from Gas Station Node to Rabbit");
    }
}
