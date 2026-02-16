package org.easybot.util;

import static org.easybot.CommonTexts.GENERAL_ERROR_PARSING_MESSAGE;
import org.easybot.dto.GasTypeDto;
import org.easybot.exceptions.ParsingException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CircleModifier extends GasTypeFormatter {
    public CircleModifier()
    {
        super("95miles", null,
                "98miles+", null,
                "Dmiles","Dmiles+",
                null, "Autogāze", null);
    }


    @Override
    public List<GasTypeDto> getFullTypeData(final List<String> rawList) throws ParsingException {
        try {
            final List<GasTypeDto> gasTypes = new ArrayList<>();
            for (int i = 0; i < rawList.size();) {
                if (isValidType(rawList, i)) {
                    gasTypes.add(GasTypeDto.builder()
                            .type(rawList.get(i++))
                            .price(rawList.get(i++))
                            .address(rawList.get(i++))
                            .build());
                } else i++;
            }
            return gasTypes;
        } catch (Exception e) {
            throw new ParsingException(String.format(GENERAL_ERROR_PARSING_MESSAGE, "Circle", e.getMessage()));
        }
    }
}
