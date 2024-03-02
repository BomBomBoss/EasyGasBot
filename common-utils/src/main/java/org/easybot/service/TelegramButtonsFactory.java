package org.easybot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Component
public class TelegramButtonsFactory {

    public InlineKeyboardMarkup createInlineButtons(Map<String, String> buttons)
    {
        List <List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List <InlineKeyboardButton> rowInLine = new ArrayList<>();

        for (Map.Entry<String, String> map : buttons.entrySet())
        {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(map.getKey());
            button.setCallbackData(map.getValue());
            rowInLine.add(button);
        }
        rowsInline.add(rowInLine);

        return new InlineKeyboardMarkup(rowsInline);
    }
}
