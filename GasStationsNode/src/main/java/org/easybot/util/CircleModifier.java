package org.easybot.util;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CircleModifier extends GasTypeFormatter {
    public CircleModifier()
    {
        super("95miles", null,
                "98miles+", null,
                "dmiles","dmiles+",
                null, "autogāze", null);
    }

    @Override
    public List<String> cleanRawElements(List<String> rawList)
    {
        return rawList;
    }
}
