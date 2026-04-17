package org.easybot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface ProduceService {

    void produceSimpleAnswer(final SendMessage sendMessage);
    void produceEditedAnswer(final EditMessageText editMessageText);
    void produceErrorReport(final SendMessage sendMessage);
}
