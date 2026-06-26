package org.easybot.util.context;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class ErrorContext {

    private final Map<Long, String> userErrors = new ConcurrentHashMap<>();
    private final String dateFormatter = "HH:mm:ss dd-MM-yyyy";

    public void registerError(final Long telegramUserId, final String errorDescription) {
        final String messageWithTime = "TIME: _%s_\n%s".formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateFormatter)), errorDescription);
        userErrors.put(telegramUserId, messageWithTime);
    }

    public Optional<String> getError(final Long telegramUserId) {
        return Optional.ofNullable(userErrors.get(telegramUserId));
    }

    public void clearError(final Long telegramUserId) {
        userErrors.remove(telegramUserId);
    }

    public void clearErrors() {
        userErrors.clear();
    }

    public boolean haveUnreadErrors() {
        return !userErrors.isEmpty();
    }

}
