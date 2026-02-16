package org.easybot.util;

import static org.easybot.CommonTexts.GENERAL_ERROR_PARSING_MESSAGE;
import org.easybot.dto.GasTypeDto;
import org.easybot.exceptions.ParsingException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<GasTypeDto> getFullTypeData(final List<String> rawList) throws ParsingException {
        try {
            final List<GasTypeDto> gasTypes = new ArrayList<>();
            for (int i = 0; i < rawList.size(); ) {
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
            throw new ParsingException(String.format(GENERAL_ERROR_PARSING_MESSAGE, "Neste", e.getMessage()));
        }
    }

}
