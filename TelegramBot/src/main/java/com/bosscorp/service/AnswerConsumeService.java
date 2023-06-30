package com.bosscorp.service;

import com.bosscorp.feature.TelegramFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.easybot.RabbitQueue.ANSWER_MESSAGE;
@Service
@Slf4j
public class AnswerConsumeService implements AnswerConsumer {


    private final TelegramBotService telegramBotService;
    private final TelegramFormatter telegramFormatter;

    public AnswerConsumeService(TelegramBotService telegramBotService, TelegramFormatter telegramFormatter)
    {
        this.telegramBotService = telegramBotService;
        this.telegramFormatter = telegramFormatter;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumeSimpleAnswer(SendMessage sendMessage)
    {
        log.info("Received answer from Rabbit");
        telegramFormatter.formatEscapeCharacters(sendMessage);
        telegramBotService.sendResponseToClient(sendMessage);
    }
}
