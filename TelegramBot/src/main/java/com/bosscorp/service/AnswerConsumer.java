package com.bosscorp.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface AnswerConsumer {
    void consumeSimpleAnswer(SendMessage sendMessage);
    void consumeEditedAnswer(EditMessageText editMessageText);
    void consumeErrors(SendMessage sendMessage);
}
