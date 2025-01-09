package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import static org.easybot.CommonTexts.CIRCLE_WITHOUT_K_TITLE;
import static org.easybot.CommonTexts.NESTE_TITLE;
import static org.easybot.CommonTexts.VIADA_TITLE;
import static org.easybot.CommonTexts.VIRSI_TITLE;
import org.easybot.dto.Error;
import org.easybot.entity.GasStationsBrands;
import static org.easybot.entity.enums.GasTypesName.DIESEL;
import static org.easybot.entity.enums.GasTypesName.TYPE_95;
import static org.easybot.entity.enums.GasTypesName.TYPE_98;
import org.easybot.entity.history.CircleHistory;
import org.easybot.entity.history.CommonHistory;
import org.easybot.entity.history.NesteHistory;
import org.easybot.entity.history.ViadaHistory;
import org.easybot.entity.history.VirsiHistory;
import org.easybot.entity.stations.CircleK;
import org.easybot.entity.stations.CommonStation;
import org.easybot.entity.stations.Neste;
import org.easybot.entity.stations.Viada;
import org.easybot.entity.stations.Virsi;
import org.easybot.enums.GasStationTitle;
import org.easybot.exceptions.ParsingException;
import org.easybot.repository.stations.GasStationsRepository;
import org.easybot.util.Modifier;
import org.easybot.util.ModifierFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GasStationService {

    private final GasStationsRepository gasStationsRepository;
    private final CommonStationService  commonStationService;
    private final CommonHistoryService commonHistoryService; ;
    private final ModifierFactory modifierFactory;
    private final List<Error> errors = new ArrayList<>();
    private final ErrorProvider errorProvider;
    private final List<CommonStation> rawListOfStations = new ArrayList<>();
    private final Map<GasStationTitle, Set<CommonStation>> freshStationData;

    private final int daysCount = 45;

    {
        freshStationData = GasStationTitle.getGasStationValues()
                .stream()
                .collect(Collectors.toMap(Function.identity(), key -> new HashSet<>()));

    }

    @Autowired
    public GasStationService(GasStationsRepository gasStationsRepository, CommonStationService commonStationService, CommonHistoryService commonHistoryService, ModifierFactory modifierFactory, ErrorProvider errorProvider)
    {
        this.gasStationsRepository = gasStationsRepository;
        this.commonStationService = commonStationService;
        this.commonHistoryService = commonHistoryService;
        this.modifierFactory = modifierFactory;
        this.errorProvider = errorProvider;
    }

    public List<GasStationsBrands> findAllBrands()
    {
        return gasStationsRepository.findAll();
    }
    public GasStationsBrands findById(Long id)
    {
        return gasStationsRepository.findById(id).orElseThrow(() -> new RuntimeException("Can't find this {" + id + "} in table"));
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void updateTablesWithDataAndHistory()
    {
        updateStationData();
        updatePriceHistory();
        purgeRedundantRows();
    }

    private void updateStationData() {
        log.info("Starting scheduled job to update station data");

        Iterator<GasStationTitle> iterable = GasStationTitle.getGasStationValues().iterator();
        GasStationTitle title;

        while (iterable.hasNext())
        {
            title = iterable.next();
            String url = title.getUrl();
            String gasStationTitle = title.name().toLowerCase();
            Modifier stationModifier = modifierFactory.createModifier(gasStationTitle);

            try
            {
                Document document = Jsoup.connect(url).get();
                Elements element = parsingWebSites(title, document);
                log.info("pulling gas prices for {}", gasStationTitle);

                Iterator<String> cleanedList = modifyList(element, stationModifier, gasStationTitle);
                log.info("Truncating table {}", title.getTitle());
                commonStationService.deleteTable(gasStationTitle);

                while (cleanedList.hasNext())
                {
                    CommonStation station = createInstance(gasStationTitle);
                    String gasType = cleanedList.next();
                    String price = cleanedList.next();
                    String location = cleanedList.next();

                    station.setGasType(stationModifier.adjustCorrectFieldTitleForDB(gasType));
                    station.setPrice(price);
                    station.setLocation(location);
                    station.setGasStationsBrands(findById(title.getId()));

                    validatePulledData(station, gasStationTitle);
                }
                rawListOfStations.clear();
            } catch (IOException | ParsingException | NoSuchElementException e)
            {
                errors.add(new Error(e));
            }
        }
        if (!errors.isEmpty())
        {
            errorProvider.printReport(errors);
        }
    }

    private void updatePriceHistory() {
        log.info("Starting scheduled job to update price history data");

        commonHistoryService.getCommonHistoryRepositoryMap()
                .forEach((key, value) -> {
                    Optional<CommonHistory> history = value.findTodayPrice(LocalDate.now());
                    Set<CommonStation> listOfPricesFromOneStation = freshStationData.get(key);
                    CommonHistory commonHistory = history.orElse(createHistoryInstance(key));

                    listOfPricesFromOneStation.forEach(station -> {
                        String gasType = station.getGasType();
                        String gasPrice = station.getPrice();


                        if (gasType.equals(TYPE_95.getDescription())) {
                            commonHistory.setPrice_95E(gasPrice);
                        } else if (gasType.equals(TYPE_98.getDescription())) {
                            commonHistory.setPrice_98E(gasPrice);
                        } else if (gasType.equals(DIESEL.getDescription())) {
                            commonHistory.setPrice_diesel(gasPrice);
                        }

                        if (commonHistory.getGasStationsBrands() == null) {
                            commonHistory.setGasStationsBrands(station.getGasStationsBrands());
                            commonHistory.setDate(LocalDate.now());
                        }
                    });
                    log.info("Saving price history for station id: {}", key.getId());
                    value.save(commonHistory);
                });
        log.info("Clearing values from price history map");
        freshStationData.forEach((key,value) -> value.clear());
    }

    private void purgeRedundantRows(){
        LocalDate threshold = LocalDate.now().minusDays(daysCount);

        commonHistoryService.getCommonHistoryRepositoryMap()
                .forEach((key, value) -> {
                    int rowsCount = value.findRowsCount();
                    if (rowsCount >= daysCount) {
                        log.info("{} station table has {} rows. Deleting redundant rows... ", key.getTitle(), rowsCount);
                        value.deleteRedundantRows(threshold);
                    }
                });
    }

    private void validatePulledData(CommonStation station, String gasStationTitle) {
        if (station.getPrice().trim().matches("\\d.\\d{3}") && !rawListOfStations.contains(station)) {
            rawListOfStations.add(station);
            final CommonStation savedStation = commonStationService.save(station, gasStationTitle);
            log.info("Gas Type - {} was saved to DB", station.getGasType());

            freshStationData.forEach((key, value) -> {
                if (key.getId() == savedStation.getGasStationsBrands().getId()) {
                    value.add(savedStation);
                }
            });
        }
    }

    private Elements parsingWebSites(GasStationTitle gasStationTitle, Document document) throws ParsingException
    {
        Elements element = document.select(gasStationTitle.getCssQuery());

        if (element.isEmpty())
        {
            String error = String.format("No data were found under cssQuery %s for gas station: %s", gasStationTitle.getCssQuery(), gasStationTitle.getTitle());
            throw new ParsingException(error);
        }
        return element;
    }

    private CommonStation createInstance(String title)
    {
        return switch (title)
                {
                    case NESTE_TITLE ->  new Neste();
                    case CIRCLE_WITHOUT_K_TITLE -> new CircleK();
                    case VIADA_TITLE -> new Viada();
                    case VIRSI_TITLE -> new Virsi();
                    default -> throw new RuntimeException("Can't create instance of gas station");
                };
    }

    private CommonHistory createHistoryInstance(GasStationTitle title)
    {
        return switch (title)
        {
            case NESTE ->  new NesteHistory();
            case CIRCLE -> new CircleHistory();
            case VIADA -> new ViadaHistory();
            case VIRSI -> new VirsiHistory();
        };
    }

    private Iterator<String> modifyList(Elements elements, Modifier stationModifier, String gasStation) throws ParsingException
    {
        List<String> list = elements.stream().map(Element::text).collect(Collectors.toList());

        list.stream().findAny().orElseThrow(()-> new ParsingException("Jsoup elements are empty for " + gasStation));

        list = stationModifier.cleanRawElements(list);
        checkForEmptyFields(list);
        return list.stream().map(x -> x.replace("EUR", "")).iterator();
    }

    private void checkForEmptyFields(List<String> list)
    {
        list.removeIf(x -> x == null || x.isEmpty());
    }

    public ModifierFactory getModifierFactory()
    {
        return modifierFactory;
    }

    public List<Error> getErrorReports() {
        return errors;
    }
}
