package org.easybot.entity.enums;

import org.apache.commons.beanutils.PropertyUtils;
import org.easybot.service.IUserModifier;
import org.easybot.util.Modifier;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public enum GasTypesName implements IUserModifier {

    TYPE_95(1L, "95E", "95E_BUTTON")
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
    TYPE_98(2L,"98E", "98E_BUTTON")
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
    DIESEL(3L,"DIESEL", "DIESEL_BUTTON")
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

    private Long id;

    private String description;

    private String buttonId;

    GasTypesName(final String description)
    {
        this.description = description;
    }

    GasTypesName(final Long id, final String description, final String buttonId) {
        this.id = id;
        this.description = description;
        this.buttonId = buttonId;
    }

    public Long getId() {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    public String getButtonId()
    {
        return buttonId;
    }

    public static Set<String> getCheapestTypesButtonId() {
        return Set.of(TYPE_95.getButtonId(), TYPE_98.getButtonId(), DIESEL.getButtonId());
    }

    public static Set<GasTypesName> getGeneralTypes() {
        return Set.of(TYPE_95, TYPE_98, DIESEL);
    }

    public static List<GasTypesName> getValues()
    {
        return List.of(GasTypesName.values());
    }

    public static String getGasTypeDescription(final Long id) {
        return Arrays.stream(values()).filter(type -> Objects.nonNull(type.getId()) && type.getId().equals(id)).map(GasTypesName::getDescription).findFirst().orElseThrow();
    }
}
