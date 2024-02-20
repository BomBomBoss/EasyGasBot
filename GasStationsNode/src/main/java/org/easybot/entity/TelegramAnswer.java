package org.easybot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static org.easybot.CommonTexts.*;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TelegramAnswer {
    private String text;
    private InlineKeyboardMarkup buttons;
    private String chatId;
    private Integer messageId;
    private TelegramUser telegramUser;

    public void formatAnswerText(List<CommonStation> list, boolean includeStationTitle)
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
                    location = RESPONSE_ALL_RIGA_DUS_EQUALS;
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
            result = UNABLE_TO_PROCEED_RESPONSE;
        }
        setText(result);
    }

    public SendMessage mapToSendMessage()
    {
        return SendMessage.builder()
                .text(text)
                .replyMarkup(buttons)
                .chatId(chatId)
                .parseMode("MarkdownV2")
                .build();
    }

    public EditMessageText mapToEditedMessage()
    {
        return EditMessageText.builder()
                .text(text)
                .replyMarkup(buttons)
                .chatId(chatId)
                .messageId(messageId)
                .parseMode("MarkdownV2")
                .build();
    }
    public void cleanButtons()
    {
        buttons = null;
    }

    public TelegramUser getTelegramUser()
    {
        return telegramUser;
    }

    public void setTelegramUser(TelegramUser telegramUser)
    {
        this.telegramUser = telegramUser;
    }
}
