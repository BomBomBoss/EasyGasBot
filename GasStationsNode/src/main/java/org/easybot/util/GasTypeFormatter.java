package org.easybot.util;

import org.easybot.entity.enums.GasTypesName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public GasTypeFormatter(String petrol95, String petrol95Plus, String petrol98, String petrol85, String diesel, String dieselPlus, String gasCNG, String gasLPG, String adBlue)
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

    Map<String, GasTypesName> init()
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

    public Map<String, GasTypesName> getTypesNameMap() {
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

    public String getPetrol95()
    {
        return petrol95;
    }

    public String getPetrol98()
    {
        return petrol98;
    }

    public String getDiesel()
    {
        return diesel;
    }

    public String getDieselPlus()
    {
        return dieselPlus;
    }

    public String getPetrol95Plus()
    {
        return petrol95Plus;
    }

    public String getPetrol85()
    {
        return petrol85;
    }

    public String getGasCNG()
    {
        return gasCNG;
    }

    public String getGasLPG()
    {
        return gasLPG;
    }

    public String getAdBlue()
    {
        return adBlue;
    }
}
