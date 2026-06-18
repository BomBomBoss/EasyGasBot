package org.easybot.service;

import org.easybot.CommonTexts;
import static org.easybot.CommonTexts.CHEAPEST_COMMAND_DISCLAIMER_LABEL;
import static org.easybot.CommonTexts.HELP_DISCLAIMER_LABEL;
import static org.easybot.CommonTexts.LANGUAGE_COMMAND_DISCLAIMER_LABEL;
import static org.easybot.CommonTexts.LANGUAGE_TO_SET_LABEL;
import static org.easybot.CommonTexts.START_COMMAND_DISCLAIMER_LABEL;
import static org.easybot.CommonTexts.START_COMMAND_PRICES_ADD;
import static org.easybot.CommonTexts.STATION_BRANDS_DISCLAIMER_LABEL;
import org.easybot.enums.BotCommands;
import org.easybot.service.telegram.TelegramAnswerFormatService;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Locale;

@SpringBootTest(classes = {TelegramAnswerFormatService.class, MessageResolver.class})
class TelegramAnswerFormatServiceTest {

    private static final String START_COMMAND = "/start";
    private static final String CHEAPEST_COMMAND = "/cheapest";
    private static final String STATION_BRANDS_COMMAND = "/brands";
    private static final String HELP_COMMAND = "/help";
    private static final String LANGUAGE_COMMAND = "/language";

    @MockitoBean
    MessageSource messageSource;

    @MockitoBean
    MessageResolver messageResolver;

    @Autowired
    private TelegramAnswerFormatService telegramAnswerFormatService;


    @Test
    void shouldInvokeResolverWithStartLabel()
    {
        BotCommands result = BotCommands.getByCommand(START_COMMAND);
        telegramAnswerFormatService.enrichStartCommand("Test", result.getDisclaimer(), Locale.ENGLISH);

        Mockito.verify(messageResolver).getLocalisedText(eq(START_COMMAND_DISCLAIMER_LABEL), any(Locale.class), any());
        Mockito.verify(messageResolver, Mockito.atMost(4)).getLocalisedText(eq(START_COMMAND_PRICES_ADD), any(Locale.class));
        Mockito.verify(messageResolver).getLocalisedText(eq(LANGUAGE_TO_SET_LABEL), any(Locale.class));
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
        Mockito.verify(messageResolver).getLocalisedText(eq(CHEAPEST_COMMAND_DISCLAIMER_LABEL), any(Locale.class));
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
        Mockito.verify(messageResolver).getLocalisedText(eq(STATION_BRANDS_DISCLAIMER_LABEL), any(Locale.class));
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
        Mockito.verify(messageResolver).getLocalisedText(eq(HELP_DISCLAIMER_LABEL), any(Locale.class));
    }

    @Test
    void shouldInvokeResolverWithLanguageLabel()
    {
        BotCommands result = BotCommands.getByCommand(LANGUAGE_COMMAND);
        telegramAnswerFormatService.resolveSimpleLocalizedResponse(result.getDisclaimer(), Locale.ENGLISH);

        Mockito.verify(messageResolver).getLocalisedText(eq(LANGUAGE_COMMAND_DISCLAIMER_LABEL), any(Locale.class));
    }





}