package com.bosscorp.controller;

import com.bosscorp.model.TelegramBotEntity;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TelegramBotAPI {
    private final TelegramBotEntity telegramBotEntity;

    @Autowired
    public TelegramBotAPI(TelegramBotEntity telegramBotEntity)
    {
        this.telegramBotEntity = telegramBotEntity;
    }
}
