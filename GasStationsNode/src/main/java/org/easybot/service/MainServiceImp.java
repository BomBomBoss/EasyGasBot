package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import static org.easybot.CommonTexts.LANGUAGE_COMMAND_DISCLAIMER_LABEL;
import static org.easybot.CommonTexts.LANGUAGE_IS_SET_LABEL;
import static org.easybot.CommonTexts.RESPONSE_NOT_SUPPORTED_UPDATE_LABEL;
import static org.easybot.CommonTexts.UNABLE_TO_PROCEED_RESPONSE_LABEL;
import org.easybot.entity.TelegramAnswer;
import org.easybot.entity.TelegramUser;
import org.easybot.entity.enums.GasTypesName;
import org.easybot.entity.stations.CommonStation;
import org.easybot.enums.AdminCommands;
import org.easybot.enums.BotCommands;
import static org.easybot.enums.BotCommands.CHEAPEST;
import static org.easybot.enums.BotCommands.HELP;
import static org.easybot.enums.BotCommands.START;
import static org.easybot.enums.BotCommands.STATION_BRANDS;
import org.easybot.enums.GasStationTitle;
import org.easybot.enums.Language;
import org.easybot.util.Modifier;
import org.easybot.wrapper.UpdateWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;


@Service
@Slf4j
public class MainServiceImp implements MainService {

    private final TelegramAnswer telegramAnswer;
    private final ProduceService produceService;
    private final CommonStationService commonStationService;
    private final GasStationService gasStationService;
    private final TelegramButtonsFactory telegramButtonsFactory;
    private final TelegramUserService telegramUserService;
    private final TelegramAnswerFormatService telegramAnswerFormatService;
    private BotCommands administrationCommand;

    public MainServiceImp(TelegramAnswer telegramAnswer,
                          ProduceService produceService,
                          CommonStationService commonStationService,
                          GasStationService gasStationService,
                          TelegramButtonsFactory telegramButtonsFactory,
                          TelegramUserService telegramUserService,
                          TelegramAnswerFormatService telegramAnswerFormatService)
    {
        this.telegramAnswer = telegramAnswer;
        this.produceService = produceService;
        this.commonStationService = commonStationService;
        this.gasStationService = gasStationService;
        this.telegramButtonsFactory = telegramButtonsFactory;
        this.telegramUserService = telegramUserService;
        this.telegramAnswerFormatService = telegramAnswerFormatService;
    }

    private final Consumer<UpdateWrapper> adminConsumer = new Consumer<>() {
        @Override
        public void accept(UpdateWrapper updateWrapper) {
                if (updateWrapper.isAdmin()) {
                    telegramAnswer.setText(administrationCommand.getDisclaimer());
                    telegramAnswer.setButtons(telegramButtonsFactory.createInlineButtons(telegramAnswerFormatService.initButtonsForAdmin()));
                }
                else
                    telegramAnswer.setText(telegramAnswerFormatService.resolveNotFoundCommand(administrationCommand.getCommand(), telegramAnswer.getTelegramUser().getLocale()));
        }
    };

    @Override
    public void processTextMessage(UpdateWrapper wrapper)
    {
        String command = wrapper.update().getMessage().getText();

        log.info("Received command: {}", command);
        administrationCommand = BotCommands.getByCommand(command);

        telegramUserService.resolveTelegramUserById(wrapper);

        TelegramUser user = telegramAnswer.getTelegramUser();
        Locale locale = user.getLocale();

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
            case LANGUAGE -> {
                telegramAnswer.setText(telegramAnswerFormatService.resolveSimpleLocalizedResponse(LANGUAGE_COMMAND_DISCLAIMER_LABEL, locale));
                telegramAnswer.setButtons(telegramButtonsFactory.createInlineButtons(telegramAnswerFormatService.initButtonsForLanguage()));
            }
            case ADMIN -> adminConsumer.accept(wrapper);
            case null, default -> getStationBrandFormattedInfo(command);
        }

        telegramAnswer.setChatId(wrapper.update().getMessage().getChatId().toString());
        produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());
    }


    @Override
    public void processCallBackQuery(UpdateWrapper wrapper)
    {
        telegramUserService.resolveTelegramUserById(wrapper);

        TelegramUser user = telegramAnswer.getTelegramUser();
        Locale locale = user.getLocale();

        String data = wrapper.update().getCallbackQuery().getData();
        telegramAnswer.cleanButtons();

        if (GasTypesName.getCheapestTypesButtonId().contains(data))
        {
            String dataWithoutButton = data.replace("_BUTTON", "").trim();
            List <CommonStation> list = getGasStationPerType(dataWithoutButton);
            Collections.sort(list);
            telegramAnswer.setText(telegramAnswerFormatService.formatAnswerText(list, true, locale));
        }

        else if (Language.getSetOfLanguageButtonId().contains(data))
        {
            String languageCode = data.replace("_BUTTON", "").toLowerCase().trim();
            user.setLanguageCode(languageCode);
            user.setLocale(Locale.of(languageCode));
            telegramUserService.updateUser(user);
            telegramAnswer.setText(telegramAnswerFormatService.resolveSimpleLocalizedResponse(LANGUAGE_IS_SET_LABEL, user.getLocale(), languageCode.toUpperCase()));
        }

        else if (AdminCommands.getSetOfAdminCommandsButtonId().contains(data))
        {
            Integer daysRange = AdminCommands.getDayRangeByButtonId(data);

            if (daysRange == 0)
            {
                List<TelegramUser> users = telegramUserService.findAllUsers();
                telegramAnswer.setText(telegramAnswerFormatService.resolveCountOfActiveUsers(users, false));
            }
            else
            {
                LocalDateTime startDate = LocalDateTime.now().minusDays(daysRange.longValue());
                List<TelegramUser> users = telegramUserService.findActiveUsersAfterDate(startDate);
                telegramAnswer.setText(telegramAnswerFormatService.resolveCountOfActiveUsers(users, true));
            }
        }

        else if (GasStationTitle.getGasStationButtonId().contains(data))
        {
            Optional<String> command = GasStationTitle.getCommandByButtonId(data);
            command.ifPresentOrElse(this::getStationBrandFormattedInfo, ()-> telegramAnswer.setText(telegramAnswerFormatService.resolveSimpleLocalizedResponse(UNABLE_TO_PROCEED_RESPONSE_LABEL, locale)));
        }

        telegramAnswer.setChatId(wrapper.update().getCallbackQuery().getMessage().getChatId().toString());
        telegramAnswer.setMessageId(wrapper.update().getCallbackQuery().getMessage().getMessageId());

        produceService.produceEditedAnswer(telegramAnswer.mapToEditedMessage());
    }

    @Override
    public void processUnsupportedUpdate(UpdateWrapper wrapper) {

        telegramUserService.resolveTelegramUserById(wrapper);

        TelegramUser user = telegramAnswer.getTelegramUser();
        Locale locale = user.getLocale();

        telegramAnswer.setText(telegramAnswerFormatService.resolveSimpleLocalizedResponse(RESPONSE_NOT_SUPPORTED_UPDATE_LABEL, locale));
        telegramAnswer.setChatId(wrapper.update().getMessage().getChatId().toString());
        produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());
    }

    private void getStationBrandFormattedInfo(String command)
    {
        Locale locale = telegramAnswer.getTelegramUser().getLocale();

        Optional<GasStationTitle> gasStation = GasStationTitle.getGasStationValues().stream()
                .filter(station -> station.getCommand().equalsIgnoreCase(command)).findFirst();

        gasStation.ifPresentOrElse(station -> telegramAnswer.setText(telegramAnswerFormatService.formatAnswerText(formatToOriginalGasTypeName(station.getTitle()), false, locale)),
                () -> telegramAnswer.setText(telegramAnswerFormatService.resolveNotFoundCommand(command, locale)));
    }

    private List<CommonStation> getGasStationInfo(String gasStationTitle)
    {
       return commonStationService.retrieveAll(gasStationTitle);
    }

    private List<CommonStation> getGasStationPerType(String type)
    {
        List <CommonStation> fullList = new ArrayList<>();
        for (GasStationTitle gs : GasStationTitle.getGasStationValues())
        {
            fullList.add(commonStationService.retrieveStationByType(gs.getTitle(), type));
        }
        return fullList;
    }

    private List<CommonStation> formatToOriginalGasTypeName(String title)
    {
        List<CommonStation> list = getGasStationInfo(title);

        if (list.isEmpty())
        {
            log.error("Returned empty list from DB with title: {}", title);
            return Collections.emptyList();
        }

        title = telegramAnswerFormatService.circleNameFormatter(title).toLowerCase();

        Modifier modifier = gasStationService.getModifierFactory().createModifier(title);

        for (CommonStation station : list)
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
