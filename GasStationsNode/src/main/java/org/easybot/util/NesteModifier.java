package org.easybot.util;

import java.util.List;

public class NesteModifier implements Modifier{
    @Override
    public List<String> cleanRawElements(List<String> rawList)
    {
        return rawList.subList(3, rawList.size());
    }
}
