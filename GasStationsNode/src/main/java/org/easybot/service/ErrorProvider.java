package org.easybot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easybot.dto.Error;
import org.easybot.entity.TelegramAnswer;
import org.easybot.service.telegram.TelegramAnswerFormatService;
import org.easybot.util.context.ErrorContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrorProvider {

    private final ProduceService produceService;
    private final TelegramAnswerFormatService telegramAnswerFormatService;
    private final ErrorContext errorContext;

    public void printGeneralReport(final List<Error> errors) {
            String errorMessage = errors.stream()
                    .reduce("", (total, element) -> total +
                                    (String.format("ERROR: *%s*\nTIME: _%s_\n\n", element.ex().getMessage(), Error.getTime())),
                            String::concat);
            errorMessage = telegramAnswerFormatService.circleNameFormatter(errorMessage);
            log.error(errorMessage);
            produceService.produceErrorReport(TelegramAnswer.mapToErrorMessage(errorMessage));
            errors.clear();
    }

    public void printUsersErrorReport() {
        if (errorContext.haveUnreadErrors()) {
            final String errorMessage = errorContext.getUserErrors().entrySet().stream().map((entry) -> """
            ERROR!
            *%s*
            User Id: *%s*
            """.formatted(entry.getValue(), entry.getKey())).collect(Collectors.joining("\n\n"));

            errorContext.clearErrors();
            log.error(errorMessage);
            produceService.produceErrorReport(TelegramAnswer.mapToErrorMessage(errorMessage));
        }
    }
}
