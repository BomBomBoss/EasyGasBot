package org.easybot.enums;

import org.easybot.CommonTexts;

public enum AdministrationCommands {
    START("/start", CommonTexts.START_COMMAND_DESCRIPTION);

    private String command;
    private String description;

    AdministrationCommands(String command, String description)
    {
        this.command = command;
        this.description = description;
    }

    public String getCommand()
    {
        return command;
    }

    public String getDescription()
    {
        return description;
    }
}
