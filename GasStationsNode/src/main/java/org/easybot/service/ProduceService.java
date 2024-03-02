package org.easybot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface ProduceService {

    void produceSimpleAnswer(SendMessage sendMessage);
    void produceEditedAnswer(EditMessageText editMessageText);
}
