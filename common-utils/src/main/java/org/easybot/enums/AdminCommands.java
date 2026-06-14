package org.easybot.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum AdminCommands {

    ONE_DAY_USERS(1, Type.USERS),
    TWO_DAYS_USERS(2, Type.USERS),
    ONE_WEEK_USERS(7, Type.USERS),
    ALL_TIME_USERS(0, Type.USERS),

    ONE_WEEK_PRICE(7, Type.PRICE),
    TWO_WEEKS_PRICE(14, Type.PRICE),
    ONE_MONTH_PRICE(30, Type.PRICE),
    ALL_TIME_PRICE(0, Type.PRICE);

    private final Type type;
    private final Integer daysRange;
    private final String buttonId;
    private final String buttonText;

    AdminCommands(final Integer daysRange, final Type type) {
        this.daysRange = daysRange;
        this.type = type;
        this.buttonId = name().concat("_BUTTON");
        this.buttonText = setButtonText(daysRange);
    }

    public static Set<String> getSetOfAdminCommandsButtonId() {
        return Arrays.stream(AdminCommands.values()).map(AdminCommands::getButtonId).collect(Collectors.toSet());
    }

    private String setButtonText(final Integer daysRange) {
        final String days = daysRange.toString();
        return switch (daysRange) {
            case 1 -> days.concat(" день");
            case 2 -> days.concat(" дня");
            case 0 -> "весь период";
            default -> days.concat(" дней");
        };
    }

    public static Integer getDayRangeByButtonId(final String buttonId) {
       return Arrays.stream(AdminCommands.values())
                .filter(command -> command.getButtonId().equals(buttonId))
                .map(AdminCommands::getDaysRange)
                .findFirst().orElse(1);
    }

    public static AdminCommands.Type getCommandType(final String buttonId) {
        return Arrays.stream(AdminCommands.values())
                .filter(command -> command.getButtonId().equals(buttonId))
                .map(AdminCommands::getType)
                .findFirst().orElseThrow();
    }

    public enum Type {
        USERS, PRICE
    }

    public static List<Integer> getPriceDaysRangeWithoutAllPeriod() {
        return Arrays.stream(values()).filter(command -> command.type == Type.PRICE && command.daysRange != 0).map(AdminCommands::getDaysRange).sorted().toList();
    }

}
