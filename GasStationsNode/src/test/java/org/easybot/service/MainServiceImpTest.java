package org.easybot.service;

import org.easybot.entity.TelegramAnswer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.easybot.CommonTexts.START_COMMAND_DISCLAIMER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MainServiceImpTest.class)
class MainServiceImpTest {

    @Autowired
    MessageSource messageSource;
    @MockBean
    TelegramAnswer telegramAnswer;
    @MockBean
    ProduceService produceService;
    @MockBean
    CommonStationService commonStationService;
    @MockBean
    GasStationService gasStationService;
    @MockBean
    TelegramButtonsFactory telegramButtonsFactory;
    @Autowired
    MainServiceImp mainServiceImp;


//    @Test
//    void startCommandDescription_ru()
//    {
//        when(telegramAnswer.getTelegramUser().getLocale()).thenReturn(new Locale("ru"));
//        when(telegramAnswer.getTelegramUser().getFirstName()).thenReturn("Mister Test Guy");
//
//        Assertions.assertEquals(START_COMMAND_DISCLAIMER, mainServiceImp.enrichStartCommand());
//    }

}