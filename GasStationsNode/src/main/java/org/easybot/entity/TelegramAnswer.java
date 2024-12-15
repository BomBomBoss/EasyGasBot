package org.easybot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

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


    public SendMessage mapToSendMessage()
    {
        return SendMessage.builder()
                .text(text)
                .replyMarkup(buttons)
                .chatId(chatId)
                .parseMode("MarkdownV2")
                .build();
    }

    public static SendMessage mapToErrorMessage(String text)
    {
        return SendMessage.builder()
                .text(text)
                .chatId("")
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

}
