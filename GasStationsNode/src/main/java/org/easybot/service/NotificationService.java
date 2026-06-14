package org.easybot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easybot.entity.TelegramAnswer;
import org.easybot.entity.TelegramUser;
import org.easybot.entity.cheapest_price.CheapestHistoryPrice;
import org.easybot.enums.AdminCommands;
import org.easybot.repository.TelegramUserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final BaseHistoryService baseHistoryService;
    private final TelegramUserRepository telegramUserRepository;
    private final TelegramAnswerFormatService telegramAnswerFormatService;
    private final ProduceService produceService;
    private final static Map<Locale, String> localizedNotifications;
    private final static long ADMIN_ID = 393223422;

    static {
        localizedNotifications = new HashMap<>();
        localizedNotifications.put(Locale.of("en"), "");
        localizedNotifications.put(Locale.of("ru"), "");
        localizedNotifications.put(Locale.of("lv"), "");
    }


    public void prepareAndSendNotification() {
        final TelegramAnswer telegramAnswer = new TelegramAnswer();

        final TelegramUser admin = telegramUserRepository.findByUserId(ADMIN_ID).orElseThrow();
        final List<TelegramUser> allUsers = Collections.singletonList(admin);

//        final List<TelegramUser> allUsers = telegramUserRepository.findAll();
        final List<Integer> daysRanges = AdminCommands.getPriceDaysRangeWithoutAllPeriod();
        localizedNotifications.replaceAll((key, value) ->
                daysRanges.stream()
                        .map(days -> {
                            final List<CheapestHistoryPrice> cheapestHistoryPrices =
                                    baseHistoryService.getHistoryPriceByRange(days);

                            return telegramAnswerFormatService
                                    .resolvePriceStatistics(cheapestHistoryPrices, days, key);
                        })
                        .collect(Collectors.joining("\n\n\n"))
        );

        allUsers.forEach(user -> {
            user.resolveLocaleFromLanguageCode(user.getLanguageCode());
            log.info("Sending price statistics notification to user {}", user.getFirstName());
            telegramAnswer.setChatId(user.getChatId().toString());
            telegramAnswer.setText(localizedNotifications.get(user.getLocale()));
            produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());

        });
    }

}
