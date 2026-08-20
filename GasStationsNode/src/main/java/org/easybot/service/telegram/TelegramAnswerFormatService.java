package org.easybot.service.telegram;

import lombok.RequiredArgsConstructor;
import org.easybot.CommonTexts;
import static org.easybot.CommonTexts.ALL_STATIONS_1;
import static org.easybot.CommonTexts.ALL_STATIONS_2;
import static org.easybot.CommonTexts.ALL_STATIONS_3;
import static org.easybot.CommonTexts.CIRCLE_K_TITLE;
import static org.easybot.CommonTexts.CIRCLE_WITHOUT_K_TITLE;
import static org.easybot.CommonTexts.EUR_SIGN_BOLD;
import static org.easybot.CommonTexts.LANGUAGE_TO_SET_LABEL;
import static org.easybot.CommonTexts.NESTE_TEMPORARILY_UNAVAILABLE_LABEL;
import static org.easybot.CommonTexts.ONE_SPACE;
import static org.easybot.CommonTexts.RESPONSE_ADDRESS_LABEL;
import static org.easybot.CommonTexts.RESPONSE_ALL_RIGA_DUS_EQUALS_LABEL;
import static org.easybot.CommonTexts.RESPONSE_COMMAND_NOT_FOUND_LABEL;
import static org.easybot.CommonTexts.RESPONSE_PRICE_LABEL;
import static org.easybot.CommonTexts.RESPONSE_STATISTICS_LABEL;
import static org.easybot.CommonTexts.START_COMMAND_PRICES_ADD;
import static org.easybot.CommonTexts.STATISTICS_ANSWER;
import static org.easybot.CommonTexts.TWO_NEW_LINES;
import static org.easybot.CommonTexts.UNABLE_TO_PROCEED_RESPONSE_LABEL;
import static org.easybot.CommonTexts.WEEKLY_NOTIFICATION_STATISTICS_LABEL;
import org.easybot.entity.TelegramUser;
import org.easybot.entity.cheapest_price.CheapestHistoryPrice;
import org.easybot.entity.enums.GasTypesName;
import static org.easybot.entity.enums.GasTypesName.DIESEL;
import static org.easybot.entity.enums.GasTypesName.TYPE_95;
import static org.easybot.entity.enums.GasTypesName.TYPE_98;
import org.easybot.entity.stations.BaseStation;
import static org.easybot.enums.AdminCommands.ALL_TIME_PRICE;
import static org.easybot.enums.AdminCommands.ALL_TIME_USERS;
import static org.easybot.enums.AdminCommands.ONE_DAY_USERS;
import static org.easybot.enums.AdminCommands.ONE_MONTH_PRICE;
import static org.easybot.enums.AdminCommands.ONE_WEEK_PRICE;
import static org.easybot.enums.AdminCommands.ONE_WEEK_USERS;
import static org.easybot.enums.AdminCommands.TWO_DAYS_USERS;
import static org.easybot.enums.AdminCommands.TWO_WEEKS_PRICE;
import org.easybot.enums.BotCommands;
import org.easybot.enums.GasStations;
import static org.easybot.enums.GasStations.CIRCLE;
import static org.easybot.enums.GasStations.NESTE;
import static org.easybot.enums.GasStations.VIADA;
import static org.easybot.enums.GasStations.VIRSI;
import static org.easybot.enums.Language.EN;
import static org.easybot.enums.Language.LV;
import static org.easybot.enums.Language.RU;
import org.easybot.service.MessageResolver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TelegramAnswerFormatService {

    private final MessageResolver messageResolver;

    private final List<Pattern> patterns =
            List.of(Pattern.compile(ALL_STATIONS_1, Pattern.CASE_INSENSITIVE),
                    Pattern.compile(ALL_STATIONS_2, Pattern.CASE_INSENSITIVE),
                    Pattern.compile(ALL_STATIONS_3, Pattern.CASE_INSENSITIVE));


    public String formatAnswerText(final List<BaseStation> list, final boolean includeStationTitle, final Locale locale)
    {
        final String result;

        if (!list.isEmpty())
        {
            final StringBuilder sb = new StringBuilder();
            for (BaseStation station : list)
            {
                final String location = getFormattedLocation(locale, station);

                if (includeStationTitle)
                {
                    final String stationTitle = circleNameFormatter(station.getGasStationsBrands().getBrandName()).toUpperCase();
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

    public String appendNesteUnavailabilityNotice(final String text, final Locale locale) {
        if (NESTE.isActive()) {
            return text;
        }
        return text + TWO_NEW_LINES + messageResolver.getLocalisedText(NESTE_TEMPORARILY_UNAVAILABLE_LABEL, locale);
    }

    private String getFormattedLocation(final Locale locale, final BaseStation station) {
        return patterns.stream()
                .filter(pattern -> pattern.matcher(station.getLocation()).find())
                .map(loc -> messageResolver.getLocalisedText(RESPONSE_ALL_RIGA_DUS_EQUALS_LABEL, locale))
                .findFirst()
                .orElse(station.getLocation());
    }

    public String formatAnswerTextWithEmoji(final String messageKey, final Locale locale) {
        final String text = messageResolver.getLocalisedText(messageKey, locale);
        return CommonTexts.parseTextWithEmoji(text);
    }

    public String enrichStartCommand(final String firstName, final String messageKey, final Locale locale) {
        final String result = messageResolver.getLocalisedText(messageKey, locale, firstName);
        final StringBuilder sb = new StringBuilder(result + TWO_NEW_LINES);
        for (GasStations gs : GasStations.getGasStationValues()) {
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

    public String resolveNotFoundCommand(final String command, final Locale locale) {
        return String.format(messageResolver.getLocalisedText(RESPONSE_COMMAND_NOT_FOUND_LABEL, locale), escapingMarkdownCharacters(command));
    }

    private String escapingMarkdownCharacters(final String unknownCommand) {
        return Objects.isNull(unknownCommand) ? "" : unknownCommand.replace("_", "").replace("*","");
    }

    public String resolveSimpleLocalizedResponse(final String messageKey, final Locale locale, final String ... arg) {
        return messageResolver.getLocalisedText(messageKey, locale, arg);
    }

    public String resolveCountOfActiveUsers(final List<TelegramUser> telegramUsers, final boolean isDetailsNeeded) {
        if (!isDetailsNeeded) {
            return String.format("Общее число пользователей за всё время: %d", telegramUsers.size());
        }

        if (telegramUsers.isEmpty()) {
            return "За этот период времени никто не пользовался ботом";
        }
        final String listOfUsers = telegramUsers.stream()
                .reduce("", (total, element) -> total +
                        (String.format("*%s*\n_%s_\n", escapingMarkdownCharacters(element.getFirstName()), getUpdateTime(element.getUpdateTime()))),
                        String::concat);

        return String.format("%s\nВсего пользователей: %s", listOfUsers, telegramUsers.size());
    }

    public String resolvePriceStatistics(final List<CheapestHistoryPrice> cheapestHistoryPrices, final int daysRange, final Locale locale) {
        final CheapestHistoryPrice cheapest95 = cheapestHistoryPrices.stream().filter(data -> TYPE_95.getId().equals(data.getGasTypeId())).min(Comparator.comparing(data -> new BigDecimal(data.getPrice()))).orElseThrow();
        final CheapestHistoryPrice cheapest98 = cheapestHistoryPrices.stream().filter(data -> TYPE_98.getId().equals(data.getGasTypeId())).min(Comparator.comparing(data -> new BigDecimal(data.getPrice()))).orElseThrow();
        final CheapestHistoryPrice cheapestDiesel = cheapestHistoryPrices.stream().filter(data -> DIESEL.getId().equals(data.getGasTypeId())).min(Comparator.comparing(data -> new BigDecimal(data.getPrice()))).orElseThrow();
        final String header = messageResolver.getLocalisedText(RESPONSE_STATISTICS_LABEL, locale, String.valueOf(daysRange));

        return STATISTICS_ANSWER.formatted(header,
                GasTypesName.getGasTypeDescription(cheapest95.getGasTypeId()), GasStations.gasStationTitle(cheapest95.getBrandId()).toUpperCase(), cheapest95.getPrice(), getDateForUsers(cheapest95.getDate()), getDayOfWeek(cheapest95.getDate(), locale),
                GasTypesName.getGasTypeDescription(cheapest98.getGasTypeId()), GasStations.gasStationTitle(cheapest98.getBrandId()).toUpperCase(), cheapest98.getPrice(), getDateForUsers(cheapest98.getDate()), getDayOfWeek(cheapest98.getDate(), locale),
                GasTypesName.getGasTypeDescription(cheapestDiesel.getGasTypeId()), GasStations.gasStationTitle(cheapestDiesel.getBrandId()).toUpperCase(), cheapestDiesel.getPrice(), getDateForUsers(cheapestDiesel.getDate()), getDayOfWeek(cheapestDiesel.getDate(), locale));
    }

    private String getUpdateTime(final LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")) : "неизвестное время";
    }

    private String getDateForUsers(final LocalDate localDate) {
        return localDate != null ? localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
    }

    private String getDayOfWeek(final LocalDate localDate, final Locale locale) {
        return localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
    }

    public String circleNameFormatter(final String title) {
        return title.replace(CIRCLE_K_TITLE, CIRCLE_WITHOUT_K_TITLE);
    }

    public String addStatisticsNotificationHeader(final Locale locale, final String message) {
        return messageResolver.getLocalisedText(WEEKLY_NOTIFICATION_STATISTICS_LABEL, locale).concat("\n\n\n\n").concat(message);
    }

    public Map<String, String> initButtonsForCheapestCommand() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(TYPE_95.getDescription(), TYPE_95.getButtonId());
        map.put(TYPE_98.getDescription(), TYPE_98.getButtonId());
        map.put(DIESEL.getDescription(), DIESEL.getButtonId());
        return map;
    }

    public Map<String, String> initButtonsForStationsBrands() {
        Map<String, String> map = new LinkedHashMap<>();
        if (NESTE.isActive()) map.put(NESTE.getTitle(), NESTE.getButtonId());
        map.put(CIRCLE.getTitle(), CIRCLE.getButtonId());
        map.put(VIRSI.getTitle(), VIRSI.getButtonId());
        map.put(VIADA.getTitle(), VIADA.getButtonId());
        return map;
    }

    public Map<String, String> initButtonsForLanguage() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(EN.getLanguage(), EN.getButtonId());
        map.put(LV.getLanguage(), LV.getButtonId());
        map.put(RU.getLanguage(), RU.getButtonId());
        return map;
    }

    public Map<String, String> initButtonsForAdmin(final BotCommands commands) {
        return switch (commands) {
            case ADMIN -> initUserStatisticsButtons();
            case STATISTICS -> initFuelStatisticsButtons();
            default -> Collections.emptyMap();
        };
    }

    private Map<String, String> initUserStatisticsButtons() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ONE_DAY_USERS.getButtonText(), ONE_DAY_USERS.getButtonId());
        map.put(TWO_DAYS_USERS.getButtonText(), TWO_DAYS_USERS.getButtonId());
        map.put(ONE_WEEK_USERS.getButtonText(), ONE_WEEK_USERS.getButtonId());
        map.put(ALL_TIME_USERS.getButtonText(), ALL_TIME_USERS.getButtonId());
        return map;
    }

    private Map<String, String> initFuelStatisticsButtons() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ONE_WEEK_PRICE.getButtonText(), ONE_WEEK_PRICE.getButtonId());
        map.put(TWO_WEEKS_PRICE.getButtonText(), TWO_WEEKS_PRICE.getButtonId());
        map.put(ONE_MONTH_PRICE.getButtonText(), ONE_MONTH_PRICE.getButtonId());
        map.put(ALL_TIME_PRICE.getButtonText(), ALL_TIME_PRICE.getButtonId());
        return map;
    }
}
