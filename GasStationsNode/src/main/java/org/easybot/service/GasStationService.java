package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import static org.easybot.CommonTexts.CIRCLE_WITHOUT_K_TITLE;
import static org.easybot.CommonTexts.NESTE_TITLE;
import static org.easybot.CommonTexts.VIADA_TITLE;
import static org.easybot.CommonTexts.VIRSI_TITLE;
import org.easybot.dto.Error;
import org.easybot.dto.GasTypeDto;
import org.easybot.entity.GasStationsBrands;
import static org.easybot.entity.enums.GasTypesName.DIESEL;
import static org.easybot.entity.enums.GasTypesName.TYPE_95;
import static org.easybot.entity.enums.GasTypesName.TYPE_98;
import org.easybot.entity.history.BaseHistory;
import org.easybot.entity.history.CircleHistory;
import org.easybot.entity.history.NesteHistory;
import org.easybot.entity.history.ViadaHistory;
import org.easybot.entity.history.VirsiHistory;
import org.easybot.entity.stations.BaseStation;
import org.easybot.entity.stations.CircleK;
import org.easybot.entity.stations.Neste;
import org.easybot.entity.stations.Viada;
import org.easybot.entity.stations.Virsi;
import org.easybot.enums.GasStations;
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
    private final BaseStationService baseStationService;
    private final StatisticsService statisticsService; ;
    private final ModifierFactory modifierFactory;
    private final List<Error> errors = new ArrayList<>();
    private final ErrorProvider errorProvider;
    private final Map<GasStations, Set<BaseStation>> latestStationData;

    private final int daysCount = 45;

    {
        latestStationData = GasStations.getGasStationValues()
                .stream()
                .collect(Collectors.toMap(Function.identity(), key -> new HashSet<>()));

    }

    @Autowired
    public GasStationService(GasStationsRepository gasStationsRepository, BaseStationService baseStationService, StatisticsService statisticsService, ModifierFactory modifierFactory, ErrorProvider errorProvider)
    {
        this.gasStationsRepository = gasStationsRepository;
        this.baseStationService = baseStationService;
        this.statisticsService = statisticsService;
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
        removeOldHistoryData();
    }

    private void updateStationData() {
        log.info("Starting scheduled job to update station data");
        for (GasStations gasStation : GasStations.getGasStationValues()) {
            final long gasStationId = gasStation.getId();
            final String url = gasStation.getUrl();
            final String gasStationTitle = gasStation.name().toLowerCase();
            final Modifier stationModifier = modifierFactory.createModifier(gasStationTitle);

            try {
                final Document document = Jsoup.connect(url).get();
                final Elements element = parsingWebSites(gasStation, document);
                log.info("pulling gas prices for {}", gasStationTitle);

                final List<GasTypeDto> gasTypesData = getGasTypesData(element, stationModifier, gasStationTitle);
                log.info("Truncating table {}", gasStation.getTitle());
                baseStationService.deleteTable(gasStationTitle);

                final List<BaseStation> stationList = gasTypesData.stream()
                        .map(data -> {
                            final BaseStation station = createInstance(gasStationTitle);
                            station.setGasType(stationModifier.adjustCorrectFieldTitleForDB(data.type()));
                            station.setPrice(getClearData(data.price()));
                            station.setLocation(getClearData(data.address()));
                            station.setGasStationsBrands(findById(gasStationId));
                            return station;
                        }).toList();

                stationList.forEach(station -> saveStationData(station, gasStationTitle));
            } catch (IOException | ParsingException | NoSuchElementException e) {
                errors.add(new Error(e));
            }
        }
        if (!errors.isEmpty()) {
            errorProvider.printReport(errors);
        }
    }

    private void updatePriceHistory() {
        log.info("Starting scheduled job to update price history data");

        statisticsService.getHistoryRepositoryMap()
                .forEach((gasStation, repository) -> {
                    final Optional<BaseHistory> history = repository.findTodayPrice(LocalDate.now());
                    final Set<BaseStation> listOfPricesFromOneStation = latestStationData.get(gasStation);
                    final BaseHistory baseHistory = history.orElse(createHistoryInstance(gasStation));

                    listOfPricesFromOneStation.forEach(station -> {
                        final String gasType = station.getGasType();
                        final String gasPrice = station.getPrice();

                        if (gasType.equals(TYPE_95.getDescription())) {
                            baseHistory.setPrice_95E(gasPrice);
                        } else if (gasType.equals(TYPE_98.getDescription())) {
                            baseHistory.setPrice_98E(gasPrice);
                        } else if (gasType.equals(DIESEL.getDescription())) {
                            baseHistory.setPrice_diesel(gasPrice);
                        }

                        if (baseHistory.getGasStationsBrands() == null) {
                            baseHistory.setGasStationsBrands(station.getGasStationsBrands());
                            baseHistory.setDate(LocalDate.now());
                        }
                    });
                    log.info("Saving price history for station id: {}", gasStation.getId());
                    repository.save(baseHistory);
                });
        log.info("Clearing values from price history map");
        latestStationData.forEach((key, value) -> value.clear());
    }

    private void removeOldHistoryData(){
        final LocalDate threshold = LocalDate.now().minusDays(daysCount);

        statisticsService.getHistoryRepositoryMap()
                .forEach((gasStation, repository) -> {
                    int rowsCount = repository.findRowsCount();
                    if (rowsCount >= daysCount) {
                        log.info("{} station table has {} rows. Deleting redundant rows... ", gasStation.getTitle(), rowsCount);
                        repository.deleteRedundantRows(threshold);
                    }
                });
    }

    private void saveStationData(final BaseStation station, final String gasStationTitle) {
            final BaseStation savedStation = baseStationService.save(station, gasStationTitle);
            log.info("Gas Type - {} was saved to DB", station.getGasType());
            latestStationData.forEach((key, value) -> {
                if (key.getId() == savedStation.getGasStationsBrands().getId()) {
                    value.add(savedStation);
                }
            });
    }

    private Elements parsingWebSites(GasStations gasStations, Document document) throws ParsingException
    {
        Elements element = document.select(gasStations.getCssQuery());

        if (element.isEmpty())
        {
            String error = String.format("No data were found under cssQuery %s for gas station: %s", gasStations.getCssQuery(), gasStations.getTitle());
            throw new ParsingException(error);
        }
        return element;
    }

    private BaseStation createInstance(final String title) {
        return switch (title)
                {
                    case NESTE_TITLE ->  new Neste();
                    case CIRCLE_WITHOUT_K_TITLE -> new CircleK();
                    case VIADA_TITLE -> new Viada();
                    case VIRSI_TITLE -> new Virsi();
                    default -> throw new RuntimeException("Can't create instance of gas station");
                };
    }

    private BaseHistory createHistoryInstance(GasStations title)
    {
        return switch (title)
        {
            case NESTE ->  new NesteHistory();
            case CIRCLE -> new CircleHistory();
            case VIADA -> new ViadaHistory();
            case VIRSI -> new VirsiHistory();
        };
    }

    private List<GasTypeDto> getGasTypesData(Elements elements, Modifier stationModifier, String gasStation) throws ParsingException {
        final List<String> list = elements.stream().map(Element::text).collect(Collectors.toList());
        list.stream().findAny().orElseThrow(()-> new ParsingException("Jsoup elements are empty for " + gasStation));
        return stationModifier.getFullTypeData(list);
    }

    private String getClearData(final String inputData) {
      return inputData.replace("EUR","").replace("[.,]$", "").trim();
    }

    public ModifierFactory getModifierFactory()
    {
        return modifierFactory;
    }

    public List<Error> getErrorReports() {
        return errors;
    }
}
