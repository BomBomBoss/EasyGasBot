package org.easybot.dto;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public record Error(Exception ex) {

    public static LocalTime getTime() {
        return LocalTime.now(ZoneOffset.UTC).plusHours(2).truncatedTo(ChronoUnit.SECONDS);
    }
}
