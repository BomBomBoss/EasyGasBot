package org.easybot.service;

import org.easybot.wrapper.UpdateWrapper;

public interface RabbitConsumer {

    void consumeTextMessage(UpdateWrapper wrapper);
    void consumeNotSupportedUpdate(UpdateWrapper wrapper);
    void consumeCallBackQuery(UpdateWrapper wrapper);
}
