package org.easybot.service;

import org.easybot.wrapper.UpdateWrapper;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface RabbitConsumer {

    void consumeTextMessage(UpdateWrapper wrapper);
    void consumePhotoMessage(UpdateWrapper wrapper);
    void consumeDocMessage(UpdateWrapper wrapper);
    void consumeUpdateException(UpdateWrapper wrapper);
    void consumeCallBackQuery(UpdateWrapper wrapper);
}
