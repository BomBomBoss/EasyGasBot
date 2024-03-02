package org.easybot.enums;

import org.easybot.CommonTexts;

import java.util.Arrays;

public enum AdministrationCommands {
    START("/start", CommonTexts.START_COMMAND_DISCLAIMER_LABEL),
    CHEAPEST("/cheapest", CommonTexts.CHEAPEST_COMMAND_DISCLAIMER_LABEL),
    STATION_BRANDS("/brands", CommonTexts.STATION_BRANDS_DISCLAIMER_LABEL),
    HELP("/help", CommonTexts.HELP_DISCLAIMER_LABEL),
    LANGUAGE("/language", CommonTexts.LANGUAGE_COMMAND_DISCLAIMER_LABEL);

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

    public static AdministrationCommands getByCommand(String command)
    {
        return Arrays.stream(AdministrationCommands.values())
                .filter(ac -> ac.command.equals(command))
                .findFirst()
                .orElse(null);
    }
}
