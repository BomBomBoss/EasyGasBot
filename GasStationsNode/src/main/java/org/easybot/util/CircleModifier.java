package org.easybot.util;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CircleModifier extends GasTypeFormatter{
    public CircleModifier()
    {
        super("miles 95", "milesPLUS 98", "miles D","milesPLUS D");
    }

    @Override
    public List<String> cleanRawElements(List<String> rawList)
    {
        return rawList;
    }
}
