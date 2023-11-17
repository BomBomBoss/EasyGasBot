package org.easybot.entity;

import org.apache.commons.beanutils.PropertyUtils;
import org.easybot.service.IUserModifier;
import org.easybot.util.Modifier;

import java.lang.reflect.InvocationTargetException;

public enum GasTypesName implements IUserModifier {

    TYPE_95("95E")
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
    TYPE_98("98E")
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
    DIESEL("Diesel")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "diesel");
                }
            },
    DIESEL_PLUS("Diesel_Plus")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "dieselPlus");
                }
            },
    GAS_LPG("Gas_LPG")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "gasLPG");
                }
            },
    GAS_CNG("Gas_CNG")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "gasCNG");
                }
            },
    AD_BLUE("AdBlue")
            {
                @Override
                public String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
                {
                    return (String) PropertyUtils.getNestedProperty(modifier, "adBlue");
                }
            };

    private String description;

    GasTypesName(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
}
