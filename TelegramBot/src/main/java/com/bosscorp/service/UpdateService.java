package com.bosscorp.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateService {

    String distributeMessageType(Update update);
}
