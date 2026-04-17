package org.easybot.service;

import org.easybot.wrapper.UpdateWrapper;

public interface RabbitConsumer {

    void consumeTextMessage(final UpdateWrapper wrapper);
    void consumeNotSupportedUpdate(final UpdateWrapper wrapper);
    void consumeCallBackQuery(final UpdateWrapper wrapper);
}
