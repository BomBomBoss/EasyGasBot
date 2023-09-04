package com.bosscorp.model;

import com.bosscorp.service.TelegramBotService;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static org.easybot.CommonTexts.*;
import static org.easybot.enums.GasStationTitle.*;

@Log4j2
@Component
public class TelegramBotEntity extends TelegramLongPollingBot {

    @Value("${botName}")
    private String botName;
    @Value("${botToken}")
    private String botToken;


    @PostConstruct
    private void createCommands()
    {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(NESTE.getCommand(), NESTE_PRICES_DESCRIPTION));
        listOfCommands.add(new BotCommand(CIRCLE.getCommand(), CIRCLE_PRICES_DESCRIPTION));
        listOfCommands.add(new BotCommand(VIADA.getCommand(), VIADA_PRICES_DESCRIPTION));
        listOfCommands.add(new BotCommand(VIRSI.getCommand(), VIRSI_PRICES_DESCRIPTION));

        SetMyCommands setMyCommands = new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null);
        try
        {
            log.info("setting command");
            this.execute(setMyCommands);
        } catch (TelegramApiException e)
        {
            log.error(e);
        }
    }

    @Lazy
    @Autowired
    private TelegramBotService consumerService;


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
