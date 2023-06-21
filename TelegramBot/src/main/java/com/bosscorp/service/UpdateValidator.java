package com.bosscorp.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateValidator {

    void contentValidate(Update update);
}
