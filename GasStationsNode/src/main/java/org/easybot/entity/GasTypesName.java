package org.easybot.entity;

import org.apache.commons.beanutils.PropertyUtils;
import org.easybot.service.IUserModifier;
import org.easybot.util.Modifier;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public enum GasTypesName implements IUserModifier {

    TYPE_95("95E", "95E_BUTTON")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "petrol95");
                }
            },
    TYPE_95_PLUS("95E_PLUS")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "petrol95Plus");
                }
            },
    TYPE_98("98E", "98E_BUTTON")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "petrol98");
                }
            },
    TYPE_85("85E")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "petrol85");
                }
            },
    DIESEL("DIESEL", "DIESEL_BUTTON")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "diesel");
                }
            },
    DIESEL_PLUS("DIESEL_PLUS")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "dieselPlus");
                }
            },
    GAS_LPG("GAS_LPG")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "gasLPG");
                }
            },
    GAS_CNG("GAS_CNG")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "gasCNG");
                }
            },
    AD_BLUE("ADBLUE")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "adBlue");
                }
            };

    private String description;

    private String buttonId;

    GasTypesName(String description)
    {
        this.description = description;
    }

    GasTypesName(String description, String buttonId)
    {
        this.description = description;
        this.buttonId = buttonId;
    }

    public String getDescription()
    {
        return description;
    }

    public String getButtonId()
    {
        return buttonId;
    }

    public static List<GasTypesName> getValues()
    {
        return List.of(GasTypesName.values());
    }
}
