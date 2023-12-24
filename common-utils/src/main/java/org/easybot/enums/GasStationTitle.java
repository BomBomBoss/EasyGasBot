package org.easybot.enums;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum GasStationTitle {
    NESTE("/neste",
            "https://www.neste.lv/lv/content/degvielas-cenas",
            "neste",
            "table > tbody > tr > td"),

    CIRCLE("/circle_k",
            "https://www.circlek.lv/privātpersonām/degvielas-cenas",
            "circle_k",
            "table > tbody > tr > td"),

    VIADA("/viada",
            "https://www.viada.lv/zemakas-degvielas-cenas/",
            "viada",
            "table > tbody > tr > td"),

    VIRSI("/virsi",
            "https://www.virsi.lv/lv/privatpersonam/degviela/degvielas-un-elektrouzlades-cenas",
            "virsi",
            "div.prices-block.fuel-block");


    private final String command;
    private final String url;
    private final String title;
    private String cssQuery;
    private final String buttonId;
    private long id;


    GasStationTitle(String command, String url, String title, String cssQuery)
    {
        this.command = command;
        this.url = url;
        this.title = title;
        this.cssQuery = cssQuery;
        this.buttonId = title.concat("_BUTTON");
    }

    public String getCommand()
    {
        return command;
    }

    public String getUrl()
    {
        return url;
    }

    public String getTitle()
    {
        return title;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getCssQuery()
    {
        return cssQuery;
    }

    public void setCssQuery(String cssQuery)
    {
        this.cssQuery = cssQuery;
    }

    public String getButtonId()
    {
        return buttonId;
    }

    @Override
    public String toString()
    {
        return "GasStationTitle{" +
                "command='" + command + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", id=" + id +
                '}';
    }

    public static List<GasStationTitle> getGasStationValues()
    {
        return List.of(GasStationTitle.values());
    }
    public static List<String> getGasStationButtonId()
    {
        return getGasStationValues().stream().map(GasStationTitle::getButtonId).toList();
    }

    public static Optional <String> getCommandByButtonId(String buttonId)
    {
        return getGasStationValues().stream().filter(station -> station.buttonId.equals(buttonId)).map(GasStationTitle::getCommand).findFirst();
    }

}
