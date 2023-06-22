package com.bosscorp.service;

import com.bosscorp.model.TelegramBotEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.easybot.RabbitQueue.DOC_MESSAGE_UPDATE;
import static org.easybot.RabbitQueue.PHOTO_MESSAGE_UPDATE;
import static org.easybot.RabbitQueue.TEXT_MESSAGE_UPDATE;
import static org.easybot.RabbitQueue.UPDATE_EXCEPTION;

@Service
@Slf4j
@Lazy
public class ConsumerServiceImpl extends TelegramBotEntity implements UpdateService {

    private final RabbitTemplate rabbitTemplate;

    public ConsumerServiceImpl(RabbitTemplate rabbitTemplate)
    {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void onUpdateReceived(Update update)
    {
        String queue = distributeMessageType(update);
        log.info("Received update Id = [{}] from chat Id = [{}]", update.getUpdateId(), update.getMessage().getChatId());
        rabbitTemplate.convertAndSend(queue,update);
    }

    @Override
    public String distributeMessageType(Update update)
    {
        Message message = update.getMessage();

        if (update.hasMessage())
        {
            if (message.hasText()) return TEXT_MESSAGE_UPDATE;
            else if (message.hasDocument()) return DOC_MESSAGE_UPDATE;
            else if (message.hasPhoto()) return PHOTO_MESSAGE_UPDATE;
        }
        return UPDATE_EXCEPTION;
    }

}


