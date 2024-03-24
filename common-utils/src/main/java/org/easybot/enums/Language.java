package org.easybot.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.easybot.CommonTexts.*;

@Getter
public enum Language {

    LV(LANGUAGE_LV),
    RU(LANGUAGE_RUS),
    EN(LANGUAGE_EN);

    private final String language;
    private final String buttonId;

    Language(String language) {
        this.language = language;
        this.buttonId = name().concat("_BUTTON");
    }

    public static Set<String> getSetOfLanguageButtonId()
    {
        return Arrays.stream(Language.values()).map(Language::getButtonId).collect(Collectors.toSet());
    }
}
