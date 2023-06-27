package org.easybot.entity;

import org.easybot.entity.GasStation;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
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

    public void formatTextFromObject(List<GasStation> list)
    {
        StringBuilder sb = new StringBuilder();

        for (GasStation gs : list)
        {
            String location = gs.locations();
            if (location.contains("Visās Rīgas DUS cenas ir vienādas") || location.contains("Visos Rīgas DUS degvielas cenas ir vienādas") || location.contains("Visās VIADA uzpildes stacijās."))
            {
                location = "Цены на всех заправках одинаковые";
            }
            sb.append(String.format("*%s*", gs.gasType())).append(", цена= ").append(String.format("*%s*", gs.price())).append(" *EUR*").append(", адрес= ").append(String.format("_%s_",location)).append("\n");
        }
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
