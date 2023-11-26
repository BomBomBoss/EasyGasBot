package org.easybot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface RabbitConsumer {

    void consumeTextMessage(Update update);
    void consumePhotoMessage(Update update);
    void consumeDocMessage(Update update);
    void consumeUpdateException(Update update);
    void consumeCallBackQuery(Update update);
}
