package com.bosscorp.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AnswerConsumer {
    void consumeSimpleAnswer(SendMessage sendMessage);
}
