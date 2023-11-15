package org.easybot.util;

import org.easybot.entity.GasTypesName;

import java.util.HashMap;
import java.util.Map;

public abstract class GasTypeFormatter implements Modifier {

    protected String nineFive;
    protected String nineEight;
    protected String diesel;
    protected String dieselPlus;
    protected Map<String, GasTypesName> typesNameMap;

    public GasTypeFormatter(String nineFive, String nineEight, String diesel, String dieselPlus)
    {
        this.nineFive = nineFive;
        this.nineEight = nineEight;
        this.diesel = diesel;
        this.dieselPlus = dieselPlus;
        typesNameMap = init();
    }

    Map<String, GasTypesName> init()
    {
        typesNameMap = new HashMap<>();
        typesNameMap.put(nineFive, GasTypesName.TYPE_95);
        typesNameMap.put(nineEight, GasTypesName.TYPE_98);
        typesNameMap.put(diesel, GasTypesName.DIESEL);
        typesNameMap.put(dieselPlus, GasTypesName.DIESEL_PLUS);
        return typesNameMap;
    }

    public Map<String, GasTypesName> getTypesNameMap()
    {
        return typesNameMap;
    }

    public String adjustCorrectFieldTitleForDB(String gasType)
    {
        gasType = gasType.toLowerCase();

        for (Map.Entry<String, GasTypesName> gasTypesNameEntry : typesNameMap.entrySet())
        {
            if (gasType.equalsIgnoreCase(gasTypesNameEntry.getKey()))
            {
                gasType = gasTypesNameEntry.getValue().getDescription();
                break;
            }
        }
        return gasType;
    }
}
