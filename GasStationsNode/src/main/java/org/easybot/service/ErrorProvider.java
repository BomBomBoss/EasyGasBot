package org.easybot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easybot.dto.Error;
import org.easybot.entity.TelegramAnswer;
import org.easybot.service.telegram.TelegramAnswerFormatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrorProvider {

    private final ProduceService produceService;
    private final TelegramAnswerFormatService telegramAnswerFormatService;

    public void printReport(final List<Error> errors) {
            String errorMessage = errors.stream()
                    .reduce("", (total, element) -> total +
                                    (String.format("ERROR: *%s*\nTIME: _%s_\n\n", element.ex().getMessage(), Error.getTime())),
                            String::concat);
            errorMessage = telegramAnswerFormatService.circleNameFormatter(errorMessage);
            log.error(errorMessage);
            produceService.produceErrorReport(TelegramAnswer.mapToErrorMessage(errorMessage));
            errors.clear();
    }
}
