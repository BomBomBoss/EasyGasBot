package org.easybot.util;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NesteModifier extends GasTypeFormatter {


    public NesteModifier()
    {
        super("Neste Futura 95", null,
                "Neste Futura 98", null,
                "Neste Futura D","Neste Pro Diesel",
                null,null,null);
    }

    @Override
    public List<String> cleanRawElements(List<String> rawList)
    {
        return rawList.subList(3, rawList.size());
    }
}
