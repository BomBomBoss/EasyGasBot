package org.easybot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProduceService {

    void produceSimpleAnswer(SendMessage sendMessage);
}
