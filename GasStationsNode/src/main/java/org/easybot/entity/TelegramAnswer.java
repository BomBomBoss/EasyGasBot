package org.easybot.entity;

import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;


@Data
public class TelegramAnswer {
    private String text;
    private InlineKeyboardMarkup buttons;
    private String chatId;
    private Integer messageId;
    private TelegramUser telegramUser;


    public SendMessage mapToSendMessage() {
        return SendMessage.builder()
                .text(text)
                .replyMarkup(buttons)
                .chatId(chatId)
                .parseMode("MarkdownV2")
                .build();
    }

    public static SendMessage mapToErrorMessage(final String text) {
        return SendMessage.builder()
                .text(text)
                .chatId("")
                .parseMode("MarkdownV2")
                .build();
    }

    public EditMessageText mapToEditedMessage() {
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
