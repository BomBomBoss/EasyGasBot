package org.easybot.service.telegram;

import org.easybot.wrapper.UpdateWrapper;

public interface MainService {
    void processTextMessage(final UpdateWrapper wrapper);

    void processCallBackQuery(final UpdateWrapper wrapper);

    void processUnsupportedUpdate(final UpdateWrapper wrapper);
}
