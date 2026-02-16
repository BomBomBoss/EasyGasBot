package org.easybot.util;

import static org.easybot.CommonTexts.GENERAL_ERROR_PARSING_MESSAGE;
import org.easybot.dto.GasTypeDto;
import org.easybot.exceptions.ParsingException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class VirsiModifier extends GasTypeFormatter {

    public VirsiModifier() {
        super("95E", null,
                "98E", null,
                "DD", null,
                "CNG", "LPG", "AdBLUE");
    }

    @Override
    public List<GasTypeDto> getFullTypeData(final List<String> rawList) throws ParsingException {
        try {
            final List<String> parsedTypes = cleanRawElements(rawList);

            final Pattern pricePattern = Pattern.compile("\\s+(\\d\\.\\d{3})\\s+");
            return getTypesNameMap().keySet()
                    .stream()
                    .flatMap(type -> {
                        final Pattern typePattern = Pattern.compile(Pattern.quote(type));
                        return parsedTypes.stream()
                                .filter(data -> typePattern.matcher(data).find())
                                .map(data -> {
                                    final Matcher priceMatcher = pricePattern.matcher(data);
                                    if (priceMatcher.find()) {
                                        final String price = priceMatcher.group(1);
                                        final String address = data.replace(type, "").replace(priceMatcher.group(0), "").trim();
                                        return GasTypeDto.builder().type(type).price(price).address(address).build();
                                    }
                                    return GasTypeDto.builder().type(type).build();
                                });
                    }).toList();
        } catch (Exception e) {
            throw new ParsingException(String.format(GENERAL_ERROR_PARSING_MESSAGE, "Virsi", e.getMessage()));
        }

    }

    public List<String> cleanRawElements(final List<String> rawList) {
        final String rawString = removeRedundantData(rawList.getFirst());

        final Map<String, Integer> typesStartingIndex = getTypesNameMap().keySet()
                .stream()
                .collect(Collectors.toMap(Function.identity(), rawString::indexOf));

        final LinkedHashMap<String, Integer> sortedByIndexTypes = typesStartingIndex.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a1, a2) -> a1,
                        LinkedHashMap::new));

        return parseToTypeStrings(rawString, sortedByIndexTypes);
    }

    private String removeRedundantData(final String rawData) {
        return rawData.replaceAll("(LV-)[0-9]{4}", "").replace("Degvielas cenas", "");
    }

    private List<String> parseToTypeStrings(final String rawString, final LinkedHashMap<String, Integer> sortedByIndexTypes) {
        final StringBuilder builder = new StringBuilder(rawString);
        return sortedByIndexTypes.values()
                .stream()
                .map(index -> {
                    final String fullTypeData = builder.substring(index);
                    builder.delete(index, builder.length());
                    return fullTypeData.trim();
                }).collect(Collectors.toList());

    }



}
