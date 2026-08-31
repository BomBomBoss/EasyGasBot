package org.easybot.service.telegram;

import lombok.extern.slf4j.Slf4j;
import static org.easybot.CommonTexts.LANGUAGE_COMMAND_DISCLAIMER_LABEL;
import static org.easybot.CommonTexts.LANGUAGE_IS_SET_LABEL;
import static org.easybot.CommonTexts.RESPONSE_NOT_SUPPORTED_UPDATE_LABEL;
import static org.easybot.CommonTexts.UNABLE_TO_PROCEED_RESPONSE_LABEL;
import org.easybot.entity.TelegramAnswer;
import org.easybot.entity.TelegramUser;
import org.easybot.entity.cheapest_price.CheapestHistoryPrice;
import org.easybot.entity.enums.GasTypesName;
import org.easybot.entity.stations.BaseStation;
import org.easybot.enums.AdminCommands;
import org.easybot.enums.BotCommands;
import static org.easybot.enums.BotCommands.CHEAPEST;
import static org.easybot.enums.BotCommands.ELECTRO;
import static org.easybot.enums.BotCommands.HELP;
import static org.easybot.enums.BotCommands.START;
import static org.easybot.enums.BotCommands.STATION_BRANDS;
import org.easybot.enums.GasStations;
import org.easybot.enums.Language;
import org.easybot.service.BaseHistoryService;
import org.easybot.service.BaseStationService;
import org.easybot.service.ChargingStationService;
import org.easybot.service.ProduceService;
import org.easybot.service.TelegramButtonsFactory;
import org.easybot.service.user.TelegramUserService;
import org.easybot.util.Modifier;
import org.easybot.util.ModifierFactory;
import org.easybot.wrapper.UpdateWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TelegramMessageService implements MainService {

    private final ProduceService produceService;
    private final BaseStationService baseStationService;
    private final BaseHistoryService baseHistoryService;
    private final TelegramButtonsFactory telegramButtonsFactory;
    private final TelegramUserService telegramUserService;
    private final TelegramAnswerFormatService telegramAnswerFormatService;
    private final ModifierFactory modifierFactory;
    private final ChargingStationService chargingStationService;
    private BotCommands administrationCommand;

    public TelegramMessageService(final ProduceService produceService,
                                  final BaseStationService baseStationService,
                                  final BaseHistoryService baseHistoryService,
                                  final TelegramButtonsFactory telegramButtonsFactory,
                                  final TelegramUserService telegramUserService,
                                  final TelegramAnswerFormatService telegramAnswerFormatService,
                                  final ModifierFactory modifierFactory,
                                  final ChargingStationService chargingStationService)
    {
        this.produceService = produceService;
        this.baseStationService = baseStationService;
        this.baseHistoryService = baseHistoryService;
        this.telegramButtonsFactory = telegramButtonsFactory;
        this.telegramUserService = telegramUserService;
        this.telegramAnswerFormatService = telegramAnswerFormatService;
        this.modifierFactory = modifierFactory;
        this.chargingStationService = chargingStationService;
    }

    private final BiConsumer<UpdateWrapper, TelegramAnswer> adminConsumer = new BiConsumer<>() {
        @Override
        public void accept(final UpdateWrapper updateWrapper, final TelegramAnswer telegramAnswer) {
                if (updateWrapper.isAdmin()) {
                    telegramAnswer.setText(administrationCommand.getDisclaimer());
                    telegramAnswer.setButtons(telegramButtonsFactory.createInlineButtons(telegramAnswerFormatService.initButtonsForAdmin(administrationCommand)));
                }
                else
                    telegramAnswer.setText(telegramAnswerFormatService.resolveNotFoundCommand(administrationCommand.getCommand(), telegramAnswer.getTelegramUser().getLocale()));
        }
    };

    @Override
    public void processTextMessage(final UpdateWrapper wrapper) {
        final TelegramAnswer telegramAnswer = new TelegramAnswer();
        final String command = wrapper.update().getMessage().getText();

        log.info("Received command: {}", command);
        administrationCommand = BotCommands.getByCommand(command);

        telegramUserService.resolveTelegramUserById(wrapper, telegramAnswer);

        final TelegramUser user = telegramAnswer.getTelegramUser();
        final Locale locale = user.getLocale();

        telegramAnswer.cleanButtons();
        switch (administrationCommand)
        {
            case START -> telegramAnswer.setText(telegramAnswerFormatService.enrichStartCommand(user.getFirstName(), START.getDisclaimer(), locale));
            case HELP -> telegramAnswer.setText(telegramAnswerFormatService.formatAnswerTextWithEmoji(HELP.getDisclaimer(), locale));
            case CHEAPEST -> {
                telegramAnswer.setText(telegramAnswerFormatService.formatAnswerTextWithEmoji(CHEAPEST.getDisclaimer(), locale));
                telegramAnswer.setButtons(telegramButtonsFactory.createInlineButtons(telegramAnswerFormatService.initButtonsForCheapestCommand()));
            }
            case STATION_BRANDS -> {
                telegramAnswer.setText(telegramAnswerFormatService.formatAnswerTextWithEmoji(STATION_BRANDS.getDisclaimer(), locale));
                telegramAnswer.setButtons(telegramButtonsFactory.createInlineButtons(telegramAnswerFormatService.initButtonsForStationsBrands()));
            }
            case ELECTRO -> telegramAnswer.setText(telegramAnswerFormatService.resolveElectroStatistics(chargingStationService.findCheapestPerDistrict(), locale));
            case LANGUAGE -> {
                telegramAnswer.setText(telegramAnswerFormatService.resolveSimpleLocalizedResponse(LANGUAGE_COMMAND_DISCLAIMER_LABEL, locale));
                telegramAnswer.setButtons(telegramButtonsFactory.createInlineButtons(telegramAnswerFormatService.initButtonsForLanguage()));
            }
            case ADMIN, STATISTICS -> adminConsumer.accept(wrapper, telegramAnswer);
            case null, default -> getStationBrandFormattedInfo(command, telegramAnswer);
        }

        telegramAnswer.setChatId(wrapper.update().getMessage().getChatId().toString());
        produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());
    }


    @Override
    public void processCallBackQuery(final UpdateWrapper wrapper) {
        final TelegramAnswer telegramAnswer = new TelegramAnswer();
        telegramUserService.resolveTelegramUserById(wrapper, telegramAnswer);

        final TelegramUser user = telegramAnswer.getTelegramUser();
        final Locale locale = user.getLocale();

        final String data = wrapper.update().getCallbackQuery().getData();
        telegramAnswer.cleanButtons();

        if (GasTypesName.getCheapestTypesButtonId().contains(data)) {
            final String dataWithoutButton = data.replace("_BUTTON", "").trim();
            final List<BaseStation> list = getGasStationPerType(dataWithoutButton);
            Collections.sort(list);
            final String formattedList = telegramAnswerFormatService.formatAnswerText(list, true, locale);
            telegramAnswer.setText(telegramAnswerFormatService.appendNesteUnavailabilityNotice(formattedList, locale));
        }

        else if (Language.getSetOfLanguageButtonId().contains(data)) {
            final String languageCode = data.replace("_BUTTON", "").toLowerCase().trim();
            user.setLanguageCode(languageCode);
            user.setLocale(Locale.of(languageCode));
            telegramUserService.updateUser(user);
            telegramAnswer.setText(telegramAnswerFormatService.resolveSimpleLocalizedResponse(LANGUAGE_IS_SET_LABEL, user.getLocale(), languageCode.toUpperCase()));
        }

        else if (AdminCommands.getSetOfAdminCommandsButtonId().contains(data)) {
            final Integer daysRange = AdminCommands.getDayRangeByButtonId(data);
            switch (AdminCommands.getCommandType(data)) {
                case USERS -> {
                    if (daysRange == 0) {
                        List<TelegramUser> users = telegramUserService.findAllUsers();
                        telegramAnswer.setText(telegramAnswerFormatService.resolveCountOfActiveUsers(users, false));
                    }
                    else {
                        LocalDateTime startDate = LocalDateTime.now().minusDays(daysRange.longValue());
                        List<TelegramUser> users = telegramUserService.findActiveUsersAfterDate(startDate);
                        telegramAnswer.setText(telegramAnswerFormatService.resolveCountOfActiveUsers(users, true));
                    }
                }
                case PRICE -> {
                    if (daysRange == 0) {
                        final List<Integer> daysRanges = AdminCommands.getPriceDaysRangeWithoutAllPeriod();
                        final String allDayRangesPriceAnswer = daysRanges.stream().map(days -> {
                            final List<CheapestHistoryPrice> cheapestHistoryPrices = baseHistoryService.getHistoryPriceByRange(days);
                            return telegramAnswerFormatService.resolvePriceStatistics(cheapestHistoryPrices, days, locale);
                        }).collect(Collectors.joining("\n\n\n"));
                        telegramAnswer.setText(allDayRangesPriceAnswer);
                    } else {
                        final List<CheapestHistoryPrice> cheapestHistoryPrices = baseHistoryService.getHistoryPriceByRange(daysRange);
                        telegramAnswer.setText(telegramAnswerFormatService.resolvePriceStatistics(cheapestHistoryPrices, daysRange, locale));
                    }
                }
            }
        }

        else if (GasStations.getGasStationButtonId().contains(data)) {
            final Optional<String> command = GasStations.getCommandByButtonId(data);
            command.ifPresentOrElse(cmd -> getStationBrandFormattedInfo(cmd, telegramAnswer), () -> telegramAnswer.setText(telegramAnswerFormatService.resolveSimpleLocalizedResponse(UNABLE_TO_PROCEED_RESPONSE_LABEL, locale)));
        }

        telegramAnswer.setChatId(wrapper.update().getCallbackQuery().getMessage().getChatId().toString());
        telegramAnswer.setMessageId(wrapper.update().getCallbackQuery().getMessage().getMessageId());

        produceService.produceEditedAnswer(telegramAnswer.mapToEditedMessage());
    }

    @Override
    public void processUnsupportedUpdate(final UpdateWrapper wrapper) {
        final TelegramAnswer telegramAnswer = new TelegramAnswer();
        telegramUserService.resolveTelegramUserById(wrapper, telegramAnswer);

        final TelegramUser user = telegramAnswer.getTelegramUser();
        final Locale locale = user.getLocale();

        telegramAnswer.setText(telegramAnswerFormatService.resolveSimpleLocalizedResponse(RESPONSE_NOT_SUPPORTED_UPDATE_LABEL, locale));
        telegramAnswer.setChatId(wrapper.update().getMessage().getChatId().toString());
        produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());
    }

    private void getStationBrandFormattedInfo(final String command, final TelegramAnswer telegramAnswer) {
        final Locale locale = telegramAnswer.getTelegramUser().getLocale();

        final Optional<GasStations> gasStation = GasStations.getGasStationValues().stream()
                .filter(station -> station.getCommand().equalsIgnoreCase(command)).findFirst();

        gasStation.ifPresentOrElse(station -> telegramAnswer.setText(telegramAnswerFormatService.formatAnswerText(formatToOriginalGasTypeName(station.getTitle()), false, locale)),
                () -> telegramAnswer.setText(telegramAnswerFormatService.resolveNotFoundCommand(command, locale)));
    }

    private List<BaseStation> getGasStationInfo(final String gasStationTitle) {
       return baseStationService.retrieveAll(gasStationTitle);
    }

    private List<BaseStation> getGasStationPerType(final String type) {
        final List <BaseStation> fullList = new ArrayList<>();
        for (GasStations gs : GasStations.getGasStationValues()) {
            fullList.add(baseStationService.retrieveStationByType(gs.getTitle(), type));
        }
        return fullList;
    }

    private List<BaseStation> formatToOriginalGasTypeName(String title)
    {
        final List<BaseStation> list = getGasStationInfo(title);

        if (list.isEmpty()) {
            log.error("Returned empty list from DB with title: {}", title);
            return Collections.emptyList();
        }

        title = telegramAnswerFormatService.circleNameFormatter(title).toLowerCase();

        final Modifier modifier = modifierFactory.createModifier(title);

        for (BaseStation station : list)
        {
            for (GasTypesName gasType : GasTypesName.getValues())
            {
                if (station.getGasType().equals(gasType.getDescription()))
                {
                    try
                    {
                        station.setGasType(gasType.getOriginalTitle(modifier));
                    } catch (Exception e)
                    {
                        log.error("Error during resolving original title via modifier");
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return list;
    }

}
