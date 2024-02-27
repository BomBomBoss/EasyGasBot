package org.easybot.service;

import org.easybot.CommonTexts;
import org.easybot.entity.CommonStation;
import org.easybot.enums.AdministrationCommands;
import org.easybot.enums.GasStationTitle;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.easybot.CommonTexts.*;
import static org.easybot.entity.enums.GasTypesName.*;
import static org.easybot.entity.enums.GasTypesName.DIESEL;
import static org.easybot.enums.GasStationTitle.*;
import static org.easybot.enums.GasStationTitle.VIADA;
import static org.easybot.enums.Language.*;

@Component
public class TelegramAnswerFormatService {

    private final MessageResolver messageResolver;

    private final Set<String> allStationsSamePrices = Set.of(ALL_RIGA_DUS_EQUALS_1, ALL_RIGA_DUS_EQUALS_2, VIADA_ALL_STATIONS, VIRSI_ALL_STATIONS);

    public TelegramAnswerFormatService(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    public String formatAnswerText(List<CommonStation> list, boolean includeStationTitle, Locale locale)
    {
        String result;

        if (!list.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            for (CommonStation gs : list)
            {
                String location = gs.getLocation();
                if (allStationsSamePrices.contains(location))
                {
                    location = messageResolver.getLocalisedTextWithoutArg(RESPONSE_ALL_RIGA_DUS_EQUALS_LABEL, locale);
                }
                if (includeStationTitle)
                {
                    String stationTitle = gs.getGasStationsBrands().getFormattedBrandName().toUpperCase();
                    sb.append(String.format("__%s__", stationTitle)) // underline
                            .append(System.lineSeparator());
                }
                sb.append(String.format("*%s*", gs.gasType)) // bold
                        .append(System.lineSeparator())
                        .append(RESPONSE_PRICE_EQUALS)
                        .append(String.format("*%s*", gs.getPrice())) // bold
                        .append(RESPONSE_EUR_SIGN_BOLD)
                        .append(System.lineSeparator())
                        .append(RESPONSE_ADDRESS_EQUALS).append(String.format("_%s_", location)) // italic
                        .append(System.lineSeparator())
                        .append(System.lineSeparator());
            }
            result = sb.toString();
        }
        else
        {
            result = messageResolver.getLocalisedTextWithoutArg(UNABLE_TO_PROCEED_RESPONSE, locale);
        }
        return result;
    }

    public String formatAnswerTextWithEmoji(String messageKey, Locale locale)
    {
        String text = messageResolver.getLocalisedTextWithoutArg(messageKey, locale);
        return CommonTexts.parseTextWithEmoji(text);
    }

    public String enrichStartCommand(String firstName, String messageKey, Locale locale)
    {
        String result = messageResolver.getLocalisedTextWithArg(messageKey, locale, firstName);
        StringBuilder sb = new StringBuilder(result + TWO_NEW_LINES);
        for(GasStationTitle gs : GasStationTitle.values())
        {
            sb.append(gs.getCommand())
                    .append(ONE_SPACE)
                    .append(messageResolver.getLocalisedTextWithoutArg(START_COMMAND_PRICES_ADD, locale))
                    .append(ONE_SPACE)
                    .append(gs.getTitle().toUpperCase())
                    .append(System.lineSeparator());
        }
        sb.append(System.lineSeparator())
                .append(AdministrationCommands.LANGUAGE.getCommand())
                .append(" - ")
                .append(messageResolver.getLocalisedTextWithoutArg(LANGUAGE_TO_SET_LABEL, locale));
        return sb.toString().replace("_", "\\_");
    }

    public String resolveNotFoundCommand(String command, Locale locale)
    {
        return String.format(messageResolver.getLocalisedTextWithoutArg(RESPONSE_COMMAND_NOT_FOUND_LABEL, locale), escapingMarkdownCharacters(command));
    }

    private String escapingMarkdownCharacters(String unknownCommand)
    {
        return unknownCommand.replace("_", "").replace("*","");
    }

    public String resolveSimpleLocalizedResponse(String messageKey, Locale locale)
    {
     return messageResolver.getLocalisedTextWithoutArg(messageKey, locale);
    }

    public String resolveSimpleLocalizedResponseWithArg(String messageKey, Locale locale, String ... arg)
    {
        return messageResolver.getLocalisedTextWithArg(messageKey, locale, arg);
    }

    public Map<String, String> initButtonsForCheapestCommand()
    {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(TYPE_95.getDescription(), TYPE_95.getButtonId());
        map.put(TYPE_98.getDescription(), TYPE_98.getButtonId());
        map.put(DIESEL.getDescription(), DIESEL.getButtonId());
        return map;
    }

    public Map<String, String> initButtonsForStationsBrands()
    {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(NESTE.getTitle(), NESTE.getButtonId());
        map.put(CIRCLE.getTitle(), CIRCLE.getButtonId());
        map.put(VIRSI.getTitle(), VIRSI.getButtonId());
        map.put(VIADA.getTitle(), VIADA.getButtonId());
        return map;
    }

    public Map<String, String> initButtonsForLanguage()
    {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(EN.getLanguage(), EN.getButtonId());
        map.put(LV.getLanguage(), LV.getButtonId());
        map.put(RU.getLanguage(), RU.getButtonId());
        return map;
    }
}
