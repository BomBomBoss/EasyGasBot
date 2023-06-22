package org.easybot.enums;

public enum GasStationTitle {
    NESTE("/neste",
            "https://www.neste.lv/lv/content/degvielas-cenas", "neste"),

    CIRCLE("/circle",
            "https://www.circlek.lv/privātpersonām/degvielas-cenas", "circle"),

    VIADA("/viada",
            "https://www.viada.lv/zemakas-degvielas-cenas/", "viada");


    private final String command;
    private final String url;
    private final String title;

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
}
