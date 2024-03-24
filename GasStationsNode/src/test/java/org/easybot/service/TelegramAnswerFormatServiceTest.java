package org.easybot.service;

import org.easybot.CommonTexts;
import org.easybot.enums.BotCommands;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.easybot.CommonTexts.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest(classes = {TelegramAnswerFormatService.class, MessageResolver.class})
class TelegramAnswerFormatServiceTest {

    private static final String START_COMMAND = "/start";
    private static final String CHEAPEST_COMMAND = "/cheapest";
    private static final String STATION_BRANDS_COMMAND = "/brands";
    private static final String HELP_COMMAND = "/help";
    private static final String LANGUAGE_COMMAND = "/language";

    @MockBean
    MessageSource messageSource;

    @MockBean
    MessageResolver messageResolver;

    @Autowired
    private TelegramAnswerFormatService telegramAnswerFormatService;


    @Test
    void shouldInvokeResolverWithStartLabel()
    {
        BotCommands result = BotCommands.getByCommand(START_COMMAND);
        telegramAnswerFormatService.enrichStartCommand("Test", result.getDisclaimer(), Locale.ENGLISH);

        Mockito.verify(messageResolver).getLocalisedTextWithArg(eq(START_COMMAND_DISCLAIMER_LABEL), any(Locale.class), any());
        Mockito.verify(messageResolver, Mockito.atMost(4)).getLocalisedTextWithoutArg(eq(START_COMMAND_PRICES_ADD), any(Locale.class));
        Mockito.verify(messageResolver).getLocalisedTextWithoutArg(eq(LANGUAGE_TO_SET_LABEL), any(Locale.class));
    }

    @Test
    void shouldInvokeResolverWithCheapestLabel()
    {
        try (MockedStatic <CommonTexts> commonTexts = Mockito.mockStatic(CommonTexts.class))
        {
            commonTexts.when(() -> CommonTexts.parseTextWithEmoji(anyString())).thenReturn(eq(anyString()));
            BotCommands result = BotCommands.getByCommand(CHEAPEST_COMMAND);
            telegramAnswerFormatService.formatAnswerTextWithEmoji(result.getDisclaimer(), Locale.ENGLISH);
        }
        Mockito.verify(messageResolver).getLocalisedTextWithoutArg(eq(CHEAPEST_COMMAND_DISCLAIMER_LABEL), any(Locale.class));
    }

    @Test
    void shouldInvokeResolverWithStationBrandLabel()
    {
        try (MockedStatic <CommonTexts> commonTexts = Mockito.mockStatic(CommonTexts.class))
        {
            commonTexts.when(() -> CommonTexts.parseTextWithEmoji(anyString())).thenReturn(eq(anyString()));
            BotCommands result = BotCommands.getByCommand(STATION_BRANDS_COMMAND);
            telegramAnswerFormatService.formatAnswerTextWithEmoji(result.getDisclaimer(), Locale.ENGLISH);
        }
        Mockito.verify(messageResolver).getLocalisedTextWithoutArg(eq(STATION_BRANDS_DISCLAIMER_LABEL), any(Locale.class));
    }

    @Test
    void shouldInvokeResolverWithHelpLabel()
    {
        try (MockedStatic <CommonTexts> commonTexts = Mockito.mockStatic(CommonTexts.class))
        {
            commonTexts.when(() -> CommonTexts.parseTextWithEmoji(anyString())).thenReturn(eq(anyString()));
            BotCommands result = BotCommands.getByCommand(HELP_COMMAND);
            telegramAnswerFormatService.formatAnswerTextWithEmoji(result.getDisclaimer(), Locale.ENGLISH);
        }
        Mockito.verify(messageResolver).getLocalisedTextWithoutArg(eq(HELP_DISCLAIMER_LABEL), any(Locale.class));
    }

    @Test
    void shouldInvokeResolverWithLanguageLabel()
    {
        BotCommands result = BotCommands.getByCommand(LANGUAGE_COMMAND);
        telegramAnswerFormatService.resolveSimpleLocalizedResponse(result.getDisclaimer(), Locale.ENGLISH);

        Mockito.verify(messageResolver).getLocalisedTextWithoutArg(eq(LANGUAGE_COMMAND_DISCLAIMER_LABEL), any(Locale.class));
    }





}