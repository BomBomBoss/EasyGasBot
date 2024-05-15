package org.easybot.util;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViadaModifier extends GasTypeFormatter {

    public ViadaModifier()
    {
        super("95 multi green", "95 multi red",
                "98 multi", "E 85",
                "Diesel","Diesel multi",
                null, "Gas", null);
    }

    @Override
    public List<String> cleanRawElements(List<String> rawList)
    {
        rawList.set(0, "95 multi green");
        rawList.set(3,"95 multi red");
        rawList.set(6, "98 multi");
        rawList.set(9, "Diesel");
        rawList.set(12, "Diesel multi");
        rawList.set(15, "Gas");
        rawList.set(18, "E 85");
        return rawList;
    }


}
