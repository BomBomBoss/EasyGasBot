package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.easybot.entity.*;

import static org.easybot.CommonTexts.*;
import static org.easybot.entity.GasTypesName.*;
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
    private final TelegramButtonsFactory telegramButtonsFactory;

    public MainServiceImp(TelegramAnswer telegramAnswer, ProduceService produceService, CommonStationService commonStationService, GasStationService gasStationService, TelegramButtonsFactory telegramButtonsFactory)
    {
        this.telegramAnswer = telegramAnswer;
        this.produceService = produceService;
        this.commonStationService = commonStationService;
        this.gasStationService = gasStationService;
        this.telegramButtonsFactory = telegramButtonsFactory;
    }

    @Override
    public void processTextMessage(Update update, String command)
    {
        if (command.equals(START.getCommand()))
        {
            telegramAnswer.setText(enrichStartCommand(update));
        }
        else if (command.equals(CHEAPEST.getCommand()))
        {
            telegramAnswer.setText(enrichCheapestCommand());
            telegramAnswer.setButtons(telegramButtonsFactory.createInlineButtons(initButtonsForCheapestCommand()));
        }
        else
        {
            Optional<GasStationTitle> gasStation = Arrays.stream(GasStationTitle.values())
                    .filter(x -> x.getCommand().equalsIgnoreCase(command)).findFirst();

            gasStation.ifPresentOrElse(station -> telegramAnswer.formatAnswerText(formatToOriginalGasTypeName(station.getTitle()), false),
                    () -> telegramAnswer.setText(String.format(RESPONSE_COMMAND_NOT_FOUND, command)));
        }
        telegramAnswer.setChatId(update.getMessage().getChatId().toString());
        produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());
    }

    @Override
    public void processCallBackQuery(Update update)
    {
        String data = update.getCallbackQuery().getData();
        telegramAnswer.cleanButtons();
        if (data.equals(TYPE_95.getButtonId()) || data.equals(TYPE_98.getButtonId()) || data.equals(DIESEL.getButtonId()))
        {
            String dataWithoutButton = data.replace("_BUTTON", "").trim();
            List <CommonStation> list = getGasStationPerType(dataWithoutButton);
            Collections.sort(list);
            telegramAnswer.formatAnswerText(list, true);
        }
        telegramAnswer.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());


    }

    private String enrichCheapestCommand()
    {
        return CHEAPEST.getDisclaimer();
    }

    private List<CommonStation> getGasStationInfo(String gasStationTitle)
    {
       return commonStationService.retrieveAll(gasStationTitle);
    }

    private List<CommonStation> getGasStationPerType(String type)
    {
        List <CommonStation> fullList = new ArrayList<>();
        for (GasStationTitle gs : GasStationTitle.getGasStationValues())
        {
            fullList.add(commonStationService.retrieveStationByType(gs.getTitle(), type));
        }
        return fullList;
    }

    private String enrichStartCommand(Update update)
    {
        String result = String.format(START.getDisclaimer(), update.getMessage().getFrom().getUserName());
        StringBuilder sb = new StringBuilder(result);
        for(GasStationTitle gs : GasStationTitle.values())
        {
            sb.append(gs.getCommand()).append(" - цены на ").append(gs.getTitle().toUpperCase()).append(System.getProperty(UTIL_LINE_SEPARATOR));
        }
        return sb.toString().replace("_", "\\_");
    }

    private List<CommonStation> formatToOriginalGasTypeName(String title)
    {
        List<CommonStation> list = getGasStationInfo(title);

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
            for (GasTypesName gasType : GasTypesName.getValues())
            {
                if (station.getGasType().equals(gasType.getDescription()))
                {
                    try
                    {
                        station.setGasType(gasType.getOriginalTitle(modifier));
                    } catch (Exception e)
                    {
                        log.error("Error during resolving original title via modifier");
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return list;
    }

    private Map<String, String> initButtonsForCheapestCommand()
    {
     Map<String, String> map = new LinkedHashMap<>();
     map.put(TYPE_95.getDescription(), TYPE_95.getButtonId());
     map.put(TYPE_98.getDescription(), TYPE_98.getButtonId());
     map.put(DIESEL.getDescription(), DIESEL.getButtonId());
     return map;
    }



}
