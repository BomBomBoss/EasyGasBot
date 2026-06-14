package org.easybot.enums;

import lombok.Getter;
import org.easybot.CommonTexts;

import java.util.Arrays;

@Getter
public enum BotCommands {

    START("/start", CommonTexts.START_COMMAND_DISCLAIMER_LABEL),
    CHEAPEST("/cheapest", CommonTexts.CHEAPEST_COMMAND_DISCLAIMER_LABEL),
    STATION_BRANDS("/brands", CommonTexts.STATION_BRANDS_DISCLAIMER_LABEL),
    HELP("/help", CommonTexts.HELP_DISCLAIMER_LABEL),
    LANGUAGE("/language", CommonTexts.LANGUAGE_COMMAND_DISCLAIMER_LABEL),
    ADMIN("/admin", CommonTexts.ADMIN_COMMAND_DISCLAIMER, true),
    STATISTICS("/stat", CommonTexts.STATISTICS_COMMAND_DISCLAIMER, true);

    private final String command;
    private final String disclaimer;
    private final Boolean isAdminCommand;

    BotCommands(final String command, final String disclaimer) {
        this.command = command;
        this.disclaimer = disclaimer;
        this.isAdminCommand = false;
    }

    BotCommands(final String command, final String disclaimer, final Boolean isAdminCommand) {
        this.command = command;
        this.disclaimer = disclaimer;
        this.isAdminCommand = isAdminCommand;
    }

    public static BotCommands getByCommand(final String command) {
        return Arrays.stream(BotCommands.values())
                .filter(ac -> ac.command.equals(command))
                .findFirst()
                .orElse(null);
    }
}
