package org.easybot.service;

import org.easybot.wrapper.UpdateWrapper;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface RabbitConsumer {

    void consumeTextMessage(UpdateWrapper wrapper);
    void consumePhotoMessage(Update update);
    void consumeDocMessage(Update update);
    void consumeUpdateException(Update update);
    void consumeCallBackQuery(UpdateWrapper wrapper);
}
