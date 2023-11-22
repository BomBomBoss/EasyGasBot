package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.easybot.entity.*;

import static org.easybot.CommonTexts.*;
import static org.easybot.enums.AdministrationCommands.*;

import org.easybot.enums.GasStationTitle;
import org.easybot.util.Modifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;


@Service
@Slf4j
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
            Optional<GasStationTitle> gasStation = Arrays.stream(GasStationTitle.values())
                    .filter(x -> x.getCommand().equalsIgnoreCase(command)).findFirst();

            gasStation.ifPresentOrElse(station -> telegramAnswer.formatAnswerText(formatToOriginalGasTypeName(station.getTitle())),
                    () -> telegramAnswer.setText(String.format(RESPONSE_COMMAND_NOT_FOUND, command)));
        }
        telegramAnswer.setChatId(update.getMessage().getChatId().toString());
        produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());


    }

    private List<CommonStation> getGasStationInfo(String gasStationTitle)
    {
       return commonStationService.retrieveAll(gasStationTitle);
    }

    private String enrichStartCommand(Update update)
    {
        String result = String.format(START.getDescription(), update.getMessage().getFrom().getUserName());
        StringBuilder sb = new StringBuilder(result);
        for(GasStationTitle gs : GasStationTitle.values())
        {
            sb.append(gs.getCommand()).append(" - цены на ").append(gs.getTitle().toUpperCase()).append(System.getProperty(UTIL_LINE_SEPARATOR));
        }
        return sb.toString().replace("_", "\\_");
    }

    private List<CommonStation> formatToOriginalGasTypeName(String title)
    {
        List <CommonStation> list = getGasStationInfo(title);

        if (list.isEmpty())
        {
            log.error("Returned empty list from DB with title: " + title);
            return Collections.emptyList();
        }

        title = title.toLowerCase();

        if (title.equals(CIRCLE_K_TITLE))
        {
           title = CIRCLE_WITHOUT_K_TITLE;
        }

        Modifier modifier = gasStationService.getModifierFactory().createModifier(title);

        for (CommonStation station : list)
        {
            for (GasTypesName gasType : GasTypesName.values())
            {
                if (station.getGasType().equals(gasType.getDescription()))
                {
                    try
                    {
                        station.setGasType(gasType.getOriginalTitle(modifier));
                    }
                    catch (Exception e)
                    {
                        log.error("Error during resolving original title via modifier");
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return list;

    }



}
