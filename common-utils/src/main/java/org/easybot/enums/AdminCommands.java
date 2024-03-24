package org.easybot.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum AdminCommands {

    ONE_DAY(1),
    TWO_DAYS(2),
    ONE_WEEK(7),
    ALL_TIME(0);

    private final Integer daysRange;
    private final String buttonId;
    private final String buttonText;

    AdminCommands(Integer daysRange) {
        this.daysRange = daysRange;
        this.buttonId = name().concat("_BUTTON");
        this.buttonText = setButtonText(daysRange);
    }

    public static Set<String> getSetOfAdminCommandsButtonId()
    {
        return Arrays.stream(AdminCommands.values()).map(AdminCommands::getButtonId).collect(Collectors.toSet());
    }

    private String setButtonText(Integer daysRange)
    {
        return switch (daysRange)
        {
            case 1 -> daysRange.toString().concat(" день");
            case 2 -> daysRange.toString().concat(" дня");
            case 0 -> "весь период";
            default -> daysRange.toString().concat(" дней");
        };
    }

    public static Integer getDayRangeByButtonId(String buttonId)
    {
       return Arrays.stream(AdminCommands.values())
                .filter(command -> command.getButtonId().equals(buttonId))
                .map(AdminCommands::getDaysRange)
                .findFirst().orElse(1);
    }

}
