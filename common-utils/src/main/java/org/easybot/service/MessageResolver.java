package org.easybot.service;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageResolver {

    private final MessageSource messageSource;

    public MessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getLocalisedTextWithoutArg(String key, Locale locale)
    {
        return messageSource.getMessage(key, null, locale);
    }

    public String getLocalisedTextWithArg(String key, Locale locale, String ... arg)
    {
        return arg == null ? getLocalisedTextWithoutArg(key, locale) : messageSource.getMessage(key, arg, locale);
    }
}
