package org.easybot.util;

import java.util.List;

public class ViadaModifier implements Modifier{
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
