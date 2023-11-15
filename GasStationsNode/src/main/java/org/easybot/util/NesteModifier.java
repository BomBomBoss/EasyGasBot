package org.easybot.util;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NesteModifier extends GasTypeFormatter {


    public NesteModifier()
    {
        super("Neste Futura 95", "Neste Futura 98", "Neste Futura D","Neste Pro Diesel");
    }

    @Override
    public List<String> cleanRawElements(List<String> rawList)
    {
        return rawList.subList(3, rawList.size());
    }
}
