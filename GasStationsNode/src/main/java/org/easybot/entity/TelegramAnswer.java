package org.easybot.entity;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static org.easybot.CommonTexts.*;

@Component
public class TelegramAnswer {
    private String text;
    private InlineKeyboardMarkup buttons;
    private String chatId;
    private Integer messageId;

    public Integer getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Integer messageId)
    {
        this.messageId = messageId;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

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
                            .append(System.getProperty(UTIL_LINE_SEPARATOR));
                }
                sb.append(String.format("*%s*", gs.gasType)) // bold
                        .append(System.getProperty(UTIL_LINE_SEPARATOR))
                        .append(RESPONSE_PRICE_EQUALS)
                        .append(String.format("*%s*", gs.getPrice())) // bold
                        .append(RESPONSE_EUR_SIGN_BOLD)
                        .append(System.getProperty(UTIL_LINE_SEPARATOR))
                        .append(RESPONSE_ADDRESS_EQUALS).append(String.format("_%s_", location)) // italic
                        .append(System.getProperty(UTIL_LINE_SEPARATOR))
                        .append(System.getProperty(UTIL_LINE_SEPARATOR));
            }
            result = sb.toString();
        }
        else
        {
            result = UNABLE_TO_PROCEED_RESPONSE;
        }
        setText(result);
    }

    public InlineKeyboardMarkup getButtons()
    {
        return buttons;
    }

    public void setButtons(InlineKeyboardMarkup buttons)
    {
        this.buttons = buttons;
    }

    public TelegramAnswer()
    {
    }

    public TelegramAnswer(String text, InlineKeyboardMarkup buttons, String chatId)
    {
        this.text = text;
        this.buttons = buttons;
        this.chatId = chatId;
    }

    public TelegramAnswer(String text, String chatId)
    {
        this.text = text;
        this.chatId = chatId;
    }

    public String getChatId()
    {
        return chatId;
    }

    public void setChatId(String chatId)
    {
        this.chatId = chatId;
    }

    public SendMessage mapToSendMessage()
    {
        return SendMessage.builder()
                .text(text)
                .replyMarkup(buttons)
                .chatId(chatId)
                .build();
    }

    public EditMessageText mapToEditedMessage()
    {
        return EditMessageText.builder()
                .text(text)
                .replyMarkup(buttons)
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }
    public void cleanButtons()
    {
        buttons = null;
    }
}
