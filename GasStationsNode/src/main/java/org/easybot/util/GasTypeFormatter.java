package org.easybot.util;

import lombok.Getter;
import org.easybot.entity.enums.GasTypesName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public abstract class GasTypeFormatter implements Modifier {

    protected String petrol95;
    protected String petrol95Plus;
    protected String petrol98;
    protected String petrol85;
    protected String diesel;
    protected String dieselPlus;
    protected String gasCNG;
    protected String gasLPG;
    protected String adBlue;
    protected Map<String, GasTypesName> typesNameMap;
    protected final Pattern pricePattern = Pattern.compile("\\s?(\\d\\.\\d{3})\\s?");

    public GasTypeFormatter(final String petrol95, final String petrol95Plus, final String petrol98, final String petrol85,
                            final String diesel, final String dieselPlus, final String gasCNG, final String gasLPG, final String adBlue)
    {
        this.petrol95 = petrol95;
        this.petrol95Plus = petrol95Plus;
        this.petrol98 = petrol98;
        this.petrol85 = petrol85;
        this.diesel = diesel;
        this.dieselPlus = dieselPlus;
        this.gasCNG = gasCNG;
        this.gasLPG = gasLPG;
        this.adBlue = adBlue;
        typesNameMap = init();
    }

    private Map<String, GasTypesName> init()
    {
        typesNameMap = new HashMap<>();
        typesNameMap.put(petrol95, GasTypesName.TYPE_95);
        typesNameMap.put(petrol95Plus, GasTypesName.TYPE_95_PLUS);
        typesNameMap.put(petrol98, GasTypesName.TYPE_98);
        typesNameMap.put(petrol85, GasTypesName.TYPE_85);
        typesNameMap.put(diesel, GasTypesName.DIESEL);
        typesNameMap.put(dieselPlus, GasTypesName.DIESEL_PLUS);
        typesNameMap.put(gasCNG, GasTypesName.GAS_CNG);
        typesNameMap.put(gasLPG, GasTypesName.GAS_LPG);
        typesNameMap.put(adBlue, GasTypesName.AD_BLUE);
        typesNameMap.remove(null);
        return typesNameMap;
    }


    public String adjustCorrectFieldTitleForDB(final String gasType) {
        final Optional<GasTypesName> type = typesNameMap.entrySet().stream()
                .filter(entry-> gasType.equalsIgnoreCase(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();

        return type.isPresent() ? type.get().getDescription() : gasType;
    }

    protected boolean isValidType(final List<String> data, final int index) {
        final int size = data.size();
        int priceIndex = index + 1;
        int addressIndex = index + 2;
        if (addressIndex < size) {
            final Matcher match = pricePattern.matcher(data.get(priceIndex));
            return getTypesNameMap().containsKey(data.get(index)) && match.find();
        }
        return false;
    }

}
