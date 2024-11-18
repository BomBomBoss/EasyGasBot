package org.easybot.service;

import org.easybot.CommonTexts;
import org.easybot.entity.CommonStation;
import org.easybot.entity.TelegramUser;
import org.easybot.enums.BotCommands;
import org.easybot.enums.GasStationTitle;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import static org.easybot.CommonTexts.*;
import static org.easybot.entity.enums.GasTypesName.*;
import static org.easybot.enums.AdminCommands.*;
import static org.easybot.enums.GasStationTitle.*;
import static org.easybot.enums.Language.*;

@Component
public class TelegramAnswerFormatService {

    private final MessageResolver messageResolver;

    private final List<Pattern> patterns =
            List.of(Pattern.compile(ALL_STATIONS_1, Pattern.CASE_INSENSITIVE),
                    Pattern.compile(ALL_STATIONS_2, Pattern.CASE_INSENSITIVE),
                    Pattern.compile(ALL_STATIONS_3, Pattern.CASE_INSENSITIVE));


    public TelegramAnswerFormatService(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    public String formatAnswerText(List<CommonStation> list, boolean includeStationTitle, Locale locale)
    {
        String result;

        if (!list.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            for (CommonStation station : list)
            {
                String location = getFormattedLocation(locale, station);

                if (includeStationTitle)
                {
                    String stationTitle = station.getGasStationsBrands().getFormattedBrandName().toUpperCase();
                    sb.append(String.format("__%s__", stationTitle)) // underline
                            .append(System.lineSeparator());
                }
                sb.append(String.format("*%s*", station.gasType)) // bold
                        .append(System.lineSeparator())
                        .append(messageResolver.getLocalisedText(RESPONSE_PRICE_LABEL, locale))
                        .append(String.format("*%s*", station.getPrice())) // bold
                        .append(EUR_SIGN_BOLD)
                        .append(System.lineSeparator())
                        .append(messageResolver.getLocalisedText(RESPONSE_ADDRESS_LABEL, locale))
                        .append(String.format("_%s_", location)) // italic
                        .append(System.lineSeparator())
                        .append(System.lineSeparator());
            }
            result = sb.toString();
        }
        else
        {
            result = messageResolver.getLocalisedText(UNABLE_TO_PROCEED_RESPONSE_LABEL, locale);
        }
        return result;
    }

    private String getFormattedLocation(Locale locale, CommonStation gs) {
        return patterns.stream()
                .filter(pattern -> pattern.matcher(gs.getLocation()).find())
                .map(loc -> messageResolver.getLocalisedText(RESPONSE_ALL_RIGA_DUS_EQUALS_LABEL, locale))
                .findFirst()
                .orElse(gs.getLocation());
    }

    public String formatAnswerTextWithEmoji(String messageKey, Locale locale)
    {
        String text = messageResolver.getLocalisedText(messageKey, locale);
        return CommonTexts.parseTextWithEmoji(text);
    }

    public String enrichStartCommand(String firstName, String messageKey, Locale locale)
    {
        String result = messageResolver.getLocalisedText(messageKey, locale, firstName);
        StringBuilder sb = new StringBuilder(result + TWO_NEW_LINES);
        for(GasStationTitle gs : GasStationTitle.values())
        {
            sb.append(gs.getCommand())
                    .append(ONE_SPACE)
                    .append(messageResolver.getLocalisedText(START_COMMAND_PRICES_ADD, locale))
                    .append(ONE_SPACE)
                    .append(gs.getTitle().toUpperCase())
                    .append(System.lineSeparator());
        }
        sb.append(System.lineSeparator())
                .append(BotCommands.LANGUAGE.getCommand())
                .append(" - ")
                .append(messageResolver.getLocalisedText(LANGUAGE_TO_SET_LABEL, locale));
        return sb.toString().replace("_", "\\_");
    }

    public String resolveNotFoundCommand(String command, Locale locale)
    {
        return String.format(messageResolver.getLocalisedText(RESPONSE_COMMAND_NOT_FOUND_LABEL, locale), escapingMarkdownCharacters(command));
    }

    private String escapingMarkdownCharacters(String unknownCommand)
    {
        return unknownCommand.replace("_", "").replace("*","");
    }

    public String resolveSimpleLocalizedResponse(String messageKey, Locale locale)
    {
     return messageResolver.getLocalisedText(messageKey, locale);
    }

    public String resolveSimpleLocalizedResponseWithArg(String messageKey, Locale locale, String ... arg)
    {
        return messageResolver.getLocalisedText(messageKey, locale, arg);
    }

    public String resolveCountOfActiveUsers(List<TelegramUser> telegramUsers, boolean isDetailsNeeded)
    {
        if (!isDetailsNeeded)
        {
            return String.format("Общее число пользователей за всё время: %d", telegramUsers.size());
        }

        if (telegramUsers.isEmpty())
        {
            return "За этот период времени никто не пользовался ботом";
        }
        String listOfUsers = telegramUsers.stream()
                .reduce(" ", (total, element) -> total +
                        (String.format("*%s*\n_%s_\n", element.getFirstName(), element.getUpdateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")))),
                        String::concat);
        return listOfUsers.concat("\n").concat("Всего пользователей: ").concat(String.valueOf(telegramUsers.size()));
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

    public Map<String, String> initButtonsForAdmin()
    {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ONE_DAY.getButtonText(), ONE_DAY.getButtonId());
        map.put(TWO_DAYS.getButtonText(), TWO_DAYS.getButtonId());
        map.put(ONE_WEEK.getButtonText(), ONE_WEEK.getButtonId());
        map.put(ALL_TIME.getButtonText(), ALL_TIME.getButtonId());
        return map;
    }
}
