package org.easybot.service;

import org.easybot.entity.*;
import static org.easybot.enums.AdministrationCommands.*;

import org.easybot.enums.GasStationTitle;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


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
        if (command.equals(START.getCommand()))
        {
            telegramAnswer.setText(enrichStartCommand(update));
        }
        else
        {
            Optional<GasStationTitle> cd = Arrays.stream(GasStationTitle.values()).filter(x -> x.getCommand().equalsIgnoreCase(command)).findFirst();
            cd.ifPresentOrElse(gasStationTitle -> telegramAnswer.formatTextFromObject(retrieveGasStationInfo(gasStationTitle.getTitle().toLowerCase())),
                    () -> telegramAnswer.setText(String.format("Command %s *NOT FOUND*. Please try another command", command)));
        }
        telegramAnswer.setChatId(update.getMessage().getChatId().toString());
        produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());


    }

    private List<CommonStation> retrieveGasStationInfo(String gasStationTitle)
    {
       return commonStationService.retrieveAll(gasStationTitle);
    }

    private String enrichStartCommand(Update update)
    {
        String result = String.format(START.getDescription(), update.getMessage().getFrom().getUserName());
        StringBuilder sb = new StringBuilder(result);
        for(GasStationTitle gs : GasStationTitle.values())
        {
            sb.append(gs.getCommand()).append(" - цены на ").append(gs.getTitle().toUpperCase()).append(System.getProperty("line.separator"));
        }
        return sb.toString().replace("_", "\\_");
    }


}
