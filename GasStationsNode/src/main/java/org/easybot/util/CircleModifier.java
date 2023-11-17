package org.easybot.util;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CircleModifier extends GasTypeFormatter{
    public CircleModifier()
    {
        super("miles 95", null,
                "milesPLUS 98", null,
                "miles D","milesPLUS D",
                null, "autogāze", null);
    }

    @Override
    public List<String> cleanRawElements(List<String> rawList)
    {
        return rawList;
    }
}
