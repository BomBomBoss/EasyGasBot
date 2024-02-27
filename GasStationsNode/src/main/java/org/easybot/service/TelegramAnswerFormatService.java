package org.easybot.service;

import org.easybot.CommonTexts;
import org.easybot.entity.CommonStation;
import org.easybot.enums.GasStationTitle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

import static org.easybot.CommonTexts.*;

@Component
public class TelegramAnswerFormatService {

    private final MessageResolver messageResolver;

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
                if (location.contains(ALL_RIGA_DUS_EQUALS_1) || location.contains(ALL_RIGA_DUS_EQUALS_2) || location.contains(VIADA_ALL_STATIONS) || location.contains(VIRSI_ALL_STATIONS))
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
        StringBuilder sb = new StringBuilder(result + "\n\n");
        for(GasStationTitle gs : GasStationTitle.values())
        {
            sb.append(gs.getCommand())
                    .append(" ")
                    .append(messageResolver.getLocalisedTextWithoutArg(START_COMMAND_PRICES_ADD, locale))
                    .append(" ")
                    .append(gs.getTitle().toUpperCase())
                    .append(System.lineSeparator());
        }
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

    public String resolveSimpleResponse(String messageKey, Locale locale)
    {
     return messageResolver.getLocalisedTextWithoutArg(messageKey, locale);
    }
}
