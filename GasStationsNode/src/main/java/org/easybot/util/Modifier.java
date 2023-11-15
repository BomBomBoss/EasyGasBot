package org.easybot.util;

import java.util.List;

public interface Modifier {

    List<String> cleanRawElements(List<String> rawList);

    String adjustCorrectFieldTitleForDB(String gasType);
}
