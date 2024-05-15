package com.bosscorp.service;

import com.bosscorp.feature.TelegramFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static org.easybot.RabbitQueue.ANSWER_EDITED_MESSAGE;
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
        sendMessage.setText(telegramFormatter.formatEscapeCharacters(sendMessage.getText()));
        telegramBotService.sendResponseToClient(sendMessage);
    }

    @Override
    @RabbitListener(queues = ANSWER_EDITED_MESSAGE)
    public void consumeEditedAnswer(EditMessageText editMessageText)
    {
        log.info("Received edited answer from Rabbit");
        editMessageText.setText(telegramFormatter.formatEscapeCharacters(editMessageText.getText()));
        telegramBotService.sendResponseWithEditedTextToClient(editMessageText);
    }
}
