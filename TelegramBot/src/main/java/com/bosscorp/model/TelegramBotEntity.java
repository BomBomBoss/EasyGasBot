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
import org.telegram.telegrambots.meta.api.methods.description.SetMyShortDescription;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static org.easybot.CommonTexts.*;
import static org.easybot.enums.BotCommands.*;

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
        List<BotCommand> listOfCommandsRu = new ArrayList<>();
        listOfCommandsRu.add(new BotCommand(STATION_BRANDS.getCommand(), BRANDS_COMMAND_DESCRIPTION));
        listOfCommandsRu.add(new BotCommand(CHEAPEST.getCommand(), CHEAPEST_COMMAND_DESCRIPTION));
        listOfCommandsRu.add(new BotCommand(HELP.getCommand(), HELP_COMMAND_DESCRIPTION));
        listOfCommandsRu.add(new BotCommand(LANGUAGE.getCommand(), LANGUAGE_COMMAND_DESCRIPTION));

        SetMyCommands setMyCommandsRu = new SetMyCommands(listOfCommandsRu, new BotCommandScopeDefault(), "ru");

        List<BotCommand> listOfCommandsEn = new ArrayList<>();
        listOfCommandsEn.add(new BotCommand(STATION_BRANDS.getCommand(), BRANDS_COMMAND_DESCRIPTION_EN));
        listOfCommandsEn.add(new BotCommand(CHEAPEST.getCommand(), CHEAPEST_COMMAND_DESCRIPTION_EN));
        listOfCommandsEn.add(new BotCommand(HELP.getCommand(), HELP_COMMAND_DESCRIPTION_EN));
        listOfCommandsEn.add(new BotCommand(LANGUAGE.getCommand(), LANGUAGE_COMMAND_DESCRIPTION_EN));

        SetMyCommands setMyCommandsEn = new SetMyCommands(listOfCommandsEn, new BotCommandScopeDefault(), null);

        SetMyShortDescription setMyShortDescriptionRu = new SetMyShortDescription(BOT_SHORT_DESCRIPTION_RU, "ru");
        SetMyShortDescription setMyShortDescriptionEn = new SetMyShortDescription(BOT_SHORT_DESCRIPTION_EN, null);
        try
        {
            log.info("setting commands");
            this.execute(setMyShortDescriptionRu);
            this.execute(setMyShortDescriptionEn);
            this.execute(setMyCommandsRu);
            this.execute(setMyCommandsEn);
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
        log.info("Received update from client");
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
