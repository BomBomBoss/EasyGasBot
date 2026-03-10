package org.easybot.service;

import org.easybot.CommonTexts;
import static org.easybot.CommonTexts.ALL_STATIONS_1;
import static org.easybot.CommonTexts.ALL_STATIONS_2;
import static org.easybot.CommonTexts.ALL_STATIONS_3;
import static org.easybot.CommonTexts.CIRCLE_K_TITLE;
import static org.easybot.CommonTexts.CIRCLE_WITHOUT_K_TITLE;
import static org.easybot.CommonTexts.EUR_SIGN_BOLD;
import static org.easybot.CommonTexts.LANGUAGE_TO_SET_LABEL;
import static org.easybot.CommonTexts.ONE_SPACE;
import static org.easybot.CommonTexts.RESPONSE_ADDRESS_LABEL;
import static org.easybot.CommonTexts.RESPONSE_ALL_RIGA_DUS_EQUALS_LABEL;
import static org.easybot.CommonTexts.RESPONSE_COMMAND_NOT_FOUND_LABEL;
import static org.easybot.CommonTexts.RESPONSE_PRICE_LABEL;
import static org.easybot.CommonTexts.START_COMMAND_PRICES_ADD;
import static org.easybot.CommonTexts.TWO_NEW_LINES;
import static org.easybot.CommonTexts.UNABLE_TO_PROCEED_RESPONSE_LABEL;
import org.easybot.entity.TelegramUser;
import static org.easybot.entity.enums.GasTypesName.DIESEL;
import static org.easybot.entity.enums.GasTypesName.TYPE_95;
import static org.easybot.entity.enums.GasTypesName.TYPE_98;
import org.easybot.entity.stations.BaseStation;
import static org.easybot.enums.AdminCommands.ALL_TIME;
import static org.easybot.enums.AdminCommands.ONE_DAY;
import static org.easybot.enums.AdminCommands.ONE_WEEK;
import static org.easybot.enums.AdminCommands.TWO_DAYS;
import org.easybot.enums.BotCommands;
import org.easybot.enums.GasStations;
import static org.easybot.enums.GasStations.CIRCLE;
import static org.easybot.enums.GasStations.NESTE;
import static org.easybot.enums.GasStations.VIADA;
import static org.easybot.enums.GasStations.VIRSI;
import static org.easybot.enums.Language.EN;
import static org.easybot.enums.Language.LV;
import static org.easybot.enums.Language.RU;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

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

    public String formatAnswerText(List<BaseStation> list, boolean includeStationTitle, Locale locale)
    {
        String result;

        if (!list.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            for (BaseStation station : list)
            {
                String location = getFormattedLocation(locale, station);

                if (includeStationTitle)
                {
                    String stationTitle = circleNameFormatter(station.getGasStationsBrands().getBrandName()).toUpperCase();
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

    private String getFormattedLocation(Locale locale, BaseStation station) {
        return patterns.stream()
                .filter(pattern -> pattern.matcher(station.getLocation()).find())
                .map(loc -> messageResolver.getLocalisedText(RESPONSE_ALL_RIGA_DUS_EQUALS_LABEL, locale))
                .findFirst()
                .orElse(station.getLocation());
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
        for (GasStations gs : GasStations.values())
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

    private String escapingMarkdownCharacters(String unknownCommand) {
        return Objects.isNull(unknownCommand) ? "" : unknownCommand.replace("_", "").replace("*","");
    }

    public String resolveSimpleLocalizedResponse(String messageKey, Locale locale, String ... arg)
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
                .reduce("", (total, element) -> total +
                        (String.format("*%s*\n_%s_\n", escapingMarkdownCharacters(element.getFirstName()), getUpdateTime(element.getUpdateTime()))),
                        String::concat);

        return String.format("%s\nВсего пользователей: %s", listOfUsers, telegramUsers.size());
    }

    private String getUpdateTime(LocalDateTime localDateTime)
    {
        return localDateTime != null ? localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")) : "неизвестное время";
    }

    public String circleNameFormatter(String title)
    {
        return title.replace(CIRCLE_K_TITLE, CIRCLE_WITHOUT_K_TITLE);
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
