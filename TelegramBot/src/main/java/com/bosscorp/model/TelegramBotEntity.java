package com.bosscorp.model;

import com.bosscorp.service.ConsumerServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j2
@Component
public class TelegramBotEntity extends TelegramLongPollingBot {

    @Value("${botName}")
    private String botName;
    @Value("${botToken}")
    private String botToken;

    @Lazy
    @Autowired
    private ConsumerServiceImpl consumerService;


    @Override
    public void onUpdateReceived(Update update)
    {
        consumerService.onUpdateReceived(update);
    }

    @Override
    public String getBotUsername()
    {
        return botName;
    }

    @Override
    public String getBotToken()
    {
        return botToken;
    }

}
