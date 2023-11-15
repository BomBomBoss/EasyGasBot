package org.easybot.entity;

public enum GasTypesName {

    TYPE_95("95E"),
    TYPE_98("98E"),
    DIESEL("Diesel"),
    DIESEL_PLUS("Diesel_Plus");

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
