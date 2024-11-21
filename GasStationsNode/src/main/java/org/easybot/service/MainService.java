package org.easybot.service;

import org.easybot.wrapper.UpdateWrapper;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void processTextMessage(UpdateWrapper wrapper);

    void processCallBackQuery(UpdateWrapper wrapper);

    void processUnsupportedUpdate(UpdateWrapper wrapper);
}
