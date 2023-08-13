package org.easybot.service;

import org.easybot.entity.*;
import org.easybot.enums.GasStationTitle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MainServiceImp implements MainService{

    private final TelegramAnswer telegramAnswer;
    private final ProduceService produceService;
    private final CommonStationService commonStationService;
    private final GasStationService gasStationService;

    public MainServiceImp(TelegramAnswer telegramAnswer, ProduceService produceService, CommonStationService commonStationService, GasStationService gasStationService)
    {
        this.telegramAnswer = telegramAnswer;
        this.produceService = produceService;
        this.commonStationService = commonStationService;
        this.gasStationService = gasStationService;
    }

    @Override
    public void processTextMessage(Update update, String command)
    {
        Optional<GasStationTitle> cd = Arrays.stream(GasStationTitle.values()).filter(x->x.getCommand().equalsIgnoreCase(command)).findFirst();
        cd.ifPresentOrElse(gasStationTitle -> telegramAnswer.formatTextFromObject(retrieveGasStationInfo(gasStationTitle.getTitle().toLowerCase())),
                ()-> telegramAnswer.setText(String.format("Command %s *NOT FOUND*. Please try another command", command)));
        telegramAnswer.setChatId(update.getMessage().getChatId().toString());
        produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());


    }

    private List<CommonStation> retrieveGasStationInfo(String gasStationTitle)
    {
       return commonStationService.retrieveAll(gasStationTitle);
    }


}
