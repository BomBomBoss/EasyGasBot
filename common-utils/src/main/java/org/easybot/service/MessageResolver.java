package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Slf4j
public class MessageResolver {

    private final MessageSource messageSource;

    public MessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getLocalisedText(String key, Locale locale, String... arg) {
        String text = "";
        try {
            text = messageSource.getMessage(key, arg, locale);
        } catch (NoSuchMessageException e) {
            log.error("Key: [{}] can't be found in resources", key);
        }
        return text;
    }
}
