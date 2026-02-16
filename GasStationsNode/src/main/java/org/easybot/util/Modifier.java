package org.easybot.util;

import org.easybot.dto.GasTypeDto;
import org.easybot.exceptions.ParsingException;

import java.util.List;

public interface Modifier {

    String adjustCorrectFieldTitleForDB(final String gasType);

    List<GasTypeDto> getFullTypeData(final List<String> rawList) throws ParsingException;
}
