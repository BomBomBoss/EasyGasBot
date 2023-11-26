package org.easybot.enums;

import java.util.List;

public enum GasStationTitle {
    NESTE("/neste",
            "https://www.neste.lv/lv/content/degvielas-cenas", "neste"),

    CIRCLE("/circle_k",
            "https://www.circlek.lv/privātpersonām/degvielas-cenas", "circle_k"),

    VIADA("/viada",
            "https://www.viada.lv/zemakas-degvielas-cenas/", "viada"),

    VIRSI("/virsi",
            "https://www.virsi.lv/lv/privatpersonam/degviela/degvielas-un-elektrouzlades-cenas", "virsi");


    private final String command;
    private final String url;
    private final String title;
    private long id;


    GasStationTitle(String command, String url, String title)
    {
        this.command = command;
        this.url = url;
        this.title = title;
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
}
