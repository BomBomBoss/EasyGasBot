package org.easybot.util;

import static org.easybot.CommonTexts.*;

public class ModifierFactory {

    public static Modifier createModifier(String title)
    {
        switch (title)
        {
            case NESTE_TITLE -> {
            return new NesteModifier();
        }
            case VIADA_TITLE -> {
            return new ViadaModifier();
        }
            case VIRSI_TITLE -> {
            return new VirsiModifier();
        }
            case CIRCLE_WITHOUT_K_TITLE -> {
            return new CircleModifier();
        }
            default -> throw new RuntimeException("Can't create modifier from title: " + title);
        }

    }
}
