package org.easybot.util;


import org.springframework.stereotype.Service;

import static org.easybot.CommonTexts.*;

@Service
public class ModifierFactory {

    private final NesteModifier nesteModifier;
    private final CircleModifier circleModifier;
    private final VirsiModifier virsiModifier;
    private final ViadaModifier viadaModifier;

    public ModifierFactory(NesteModifier nesteModifier, CircleModifier circleModifier, VirsiModifier virsiModifier, ViadaModifier viadaModifier)
    {
        this.nesteModifier = nesteModifier;
        this.circleModifier = circleModifier;
        this.virsiModifier = virsiModifier;
        this.viadaModifier = viadaModifier;
    }

    public Modifier createModifier(String title)
    {
        switch (title)
        {
            case NESTE_TITLE -> {
            return nesteModifier;
        }
            case VIADA_TITLE -> {
            return viadaModifier;
        }
            case VIRSI_TITLE -> {
            return virsiModifier;
        }
            case CIRCLE_WITHOUT_K_TITLE -> {
            return circleModifier;
        }
            default -> throw new RuntimeException("Can't create modifier from title: " + title);
        }

    }
}
