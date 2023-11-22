package com.bosscorp.feature;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
public class TelegramFormatter {

    static final List<String> escapeCharacters = Arrays.asList("[", "]", "(", ")", "~", "`", ">", "#", "+", "-", "=", "|", "{", "}", ".", "!");

    public void formatEscapeCharacters(SendMessage sendMessage)
    {
        String text = sendMessage.getText();
        text = escapeCharacters.stream()
                .map(toReplace -> (Function<String, String>) s -> s.replace(toReplace, String.format("\\%s", toReplace)))
                .reduce(Function.identity(), Function::andThen)
                .apply(text);
        sendMessage.setText(text);
    }
}
