package org.easybot.util;

import static org.easybot.CommonTexts.GENERAL_ERROR_PARSING_MESSAGE;
import org.easybot.dto.GasTypeDto;
import org.easybot.exceptions.ParsingException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<GasTypeDto> getFullTypeData(final List<String> rawList) throws ParsingException {
        try {
            setTypeNames(rawList);
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
            throw new ParsingException(String.format(GENERAL_ERROR_PARSING_MESSAGE, "Viada", e.getMessage()));
        }
    }

    private void setTypeNames(final List<String> rawList) {
        rawList.set(0, "95 multi green");
        rawList.set(3,"95 multi red");
        rawList.set(6, "98 multi");
        rawList.set(9, "Diesel");
        rawList.set(12, "Diesel multi");
        rawList.set(15, "Gas");
        rawList.set(18, "E 85");
    }



}
