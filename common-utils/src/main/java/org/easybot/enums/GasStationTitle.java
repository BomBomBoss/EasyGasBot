package org.easybot.enums;

public enum GasStationTitle {
    NESTE("/neste",
            "https://www.neste.lv/lv/content/degvielas-cenas", "neste"),

    CIRCLE("/circle_k",
            "https://www.circlek.lv/privātpersonām/degvielas-cenas", "circle_k"),

    VIADA("/viada",
            "https://www.viada.lv/zemakas-degvielas-cenas/", "viada");


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
}
