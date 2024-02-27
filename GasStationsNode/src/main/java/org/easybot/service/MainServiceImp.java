package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.easybot.CommonTexts;
import org.easybot.entity.*;

import static org.easybot.CommonTexts.*;
import static org.easybot.entity.enums.GasTypesName.*;
import static org.easybot.enums.AdministrationCommands.*;
import static org.easybot.enums.GasStationTitle.*;

import org.easybot.entity.enums.GasTypesName;
import org.easybot.enums.GasStationTitle;
import org.easybot.util.Modifier;
import org.easybot.wrapper.UpdateWrapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;


@Service
@Slf4j
public class MainServiceImp implements MainService {

    private final TelegramAnswer telegramAnswer;
    private final ProduceService produceService;
    private final CommonStationService commonStationService;
    private final GasStationService gasStationService;
    private final TelegramButtonsFactory telegramButtonsFactory;
    private final TelegramUserService telegramUserService;
    private final TelegramAnswerFormatService telegramAnswerFormatService;

    public MainServiceImp(TelegramAnswer telegramAnswer,
                          ProduceService produceService,
                          CommonStationService commonStationService,
                          GasStationService gasStationService,
                          TelegramButtonsFactory telegramButtonsFactory,
                          TelegramUserService telegramUserService,
                          TelegramAnswerFormatService telegramAnswerFormatService)
    {
        this.telegramAnswer = telegramAnswer;
        this.produceService = produceService;
        this.commonStationService = commonStationService;
        this.gasStationService = gasStationService;
        this.telegramButtonsFactory = telegramButtonsFactory;
        this.telegramUserService = telegramUserService;
        this.telegramAnswerFormatService = telegramAnswerFormatService;
    }

    @Override
    public void processTextMessage(UpdateWrapper wrapper, String command)
    {
        telegramUserService.resolveTelegramUserById(wrapper.user());

        TelegramUser user = telegramAnswer.getTelegramUser();
        Locale locale = user.getLocale();

        telegramAnswer.cleanButtons();

        if (command.equals(START.getCommand()))
        {
            telegramAnswer.setText(telegramAnswerFormatService.enrichStartCommand(user.getFirstName(), START.getDisclaimer(), locale));
        }
        else if (command.equals(HELP.getCommand()))
        {
            telegramAnswer.setText(telegramAnswerFormatService.formatAnswerTextWithEmoji(HELP.getDisclaimer(), locale));
        }
        else if (command.equals(CHEAPEST.getCommand()))
        {
            telegramAnswer.setText(telegramAnswerFormatService.formatAnswerTextWithEmoji(CHEAPEST.getDisclaimer(), locale));
            telegramAnswer.setButtons(telegramButtonsFactory.createInlineButtons(initButtonsForCheapestCommand()));
        }
        else if (command.equals(STATION_BRANDS.getCommand()))
        {
            telegramAnswer.setText(telegramAnswerFormatService.formatAnswerTextWithEmoji(STATION_BRANDS.getDisclaimer(), locale));
            telegramAnswer.setButtons(telegramButtonsFactory.createInlineButtons(initButtonsForStationsBrands()));
        }
        else
        {
            getStationBrandFormattedInfo(command);
        }
        telegramAnswer.setChatId(wrapper.update().getMessage().getChatId().toString());
        produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());
    }

    private void getStationBrandFormattedInfo(String command)
    {
        Locale locale = telegramAnswer.getTelegramUser().getLocale();

        Optional<GasStationTitle> gasStation = GasStationTitle.getGasStationValues().stream()
                .filter(x -> x.getCommand().equalsIgnoreCase(command)).findFirst();

        gasStation.ifPresentOrElse(station -> telegramAnswer.setText(telegramAnswerFormatService.formatAnswerText(formatToOriginalGasTypeName(station.getTitle()), false, locale)),
                () -> telegramAnswer.setText(telegramAnswerFormatService.resolveNotFoundCommand(command, locale)));
    }


    @Override
    public void processCallBackQuery(UpdateWrapper wrapper)
    {
        telegramUserService.resolveTelegramUserById(wrapper.user());

        TelegramUser user = telegramAnswer.getTelegramUser();
        Locale locale = user.getLocale();

        String data = wrapper.update().getCallbackQuery().getData();
        telegramAnswer.cleanButtons();

        if (data.equals(TYPE_95.getButtonId()) || data.equals(TYPE_98.getButtonId()) || data.equals(DIESEL.getButtonId()))
        {
            String dataWithoutButton = data.replace("_BUTTON", "").trim();
            List <CommonStation> list = getGasStationPerType(dataWithoutButton);
            Collections.sort(list);
            telegramAnswer.setText(telegramAnswerFormatService.formatAnswerText(list, true, locale));
        }
        if (GasStationTitle.getGasStationButtonId().contains(data))
        {
            Optional<String> command = GasStationTitle.getCommandByButtonId(data);
            command.ifPresentOrElse(this::getStationBrandFormattedInfo, ()-> telegramAnswer.setText(telegramAnswerFormatService.resolveSimpleResponse(UNABLE_TO_PROCEED_RESPONSE_LABEL, locale)));
        }
        telegramAnswer.setChatId(wrapper.update().getCallbackQuery().getMessage().getChatId().toString());
        telegramAnswer.setMessageId(wrapper.update().getCallbackQuery().getMessage().getMessageId());

        produceService.produceEditedAnswer(telegramAnswer.mapToEditedMessage());
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

    private Map<String, String> initButtonsForStationsBrands()
    {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(NESTE.getTitle(), NESTE.getButtonId());
        map.put(CIRCLE.getTitle(), CIRCLE.getButtonId());
        map.put(VIRSI.getTitle(), VIRSI.getButtonId());
        map.put(VIADA.getTitle(), VIADA.getButtonId());
        return map;
    }


}
