package org.easybot.enums;

import org.easybot.CommonTexts;

public enum AdministrationCommands {
    START("/start", CommonTexts.START_COMMAND_DISCLAIMER_LABEL),
    CHEAPEST("/cheapest", CommonTexts.CHEAPEST_COMMAND_DISCLAIMER),
    STATION_BRANDS("/brands", CommonTexts.STATION_BRANDS_DISCLAIMER),
    HELP("/help", CommonTexts.STATION_BRANDS_DISCLAIMER);

    private String command;
    private String disclaimer;

    AdministrationCommands(String command, String disclaimer)
    {
        this.command = command;
        this.disclaimer = disclaimer;
    }

    public String getCommand()
    {
        return command;
    }

    public String getDisclaimer()
    {
        return disclaimer;
    }
}
