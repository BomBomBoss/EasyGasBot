package org.easybot.service.jobs;

import lombok.extern.slf4j.Slf4j;
import org.easybot.dto.Error;
import org.easybot.dto.GasTypeDto;
import org.easybot.entity.GasStationsBrands;
import static org.easybot.entity.enums.GasTypesName.DIESEL;
import static org.easybot.entity.enums.GasTypesName.TYPE_95;
import static org.easybot.entity.enums.GasTypesName.TYPE_98;
import org.easybot.entity.history.BaseHistory;
import org.easybot.entity.stations.BaseStation;
import org.easybot.enums.GasStations;
import org.easybot.exceptions.ParsingException;
import org.easybot.factory.BaseFactory;
import org.easybot.repository.history.BaseHistoryRepository;
import org.easybot.repository.stations.GasStationsRepository;
import org.easybot.service.BaseHistoryService;
import org.easybot.service.BaseStationService;
import org.easybot.service.ErrorProvider;
import org.easybot.service.notification.NotificationService;
import org.easybot.util.Modifier;
import org.easybot.util.ModifierFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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
public class GasStationJobsService {

    private final GasStationsRepository gasStationsRepository;
    private final BaseStationService baseStationService;
    private final BaseHistoryService baseHistoryService;
    private final NotificationService notificationService;
    private final BaseFactory baseFactory;
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
    public GasStationJobsService(final GasStationsRepository gasStationsRepository,
                                 final BaseStationService baseStationService,
                                 final NotificationService notificationService,
                                 final ModifierFactory modifierFactory,
                                 final ErrorProvider errorProvider,
                                 final BaseHistoryService baseHistoryService,
                                 final BaseFactory baseFactory)
    {
        this.gasStationsRepository = gasStationsRepository;
        this.baseStationService = baseStationService;
        this.baseHistoryService = baseHistoryService;
        this.notificationService = notificationService;
        this.modifierFactory = modifierFactory;
        this.errorProvider = errorProvider;
        this.baseFactory = baseFactory;
    }

    public GasStationsBrands findById(final Long id) {
        return gasStationsRepository.findById(id).orElseThrow(() -> new RuntimeException("Can't find this {" + id + "} in table"));
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void updateTablesWithDataAndHistory() {
        updateStationData();
        updatePriceHistory();
        removeOldHistoryData();
    }

    @Scheduled(cron = "0 0 11 * * *")
    public void updateHistoryPrice() {
        try {
            log.info("Scheduled job: Updating history prices");
            baseHistoryService.updateCheapestHistoryPrice();
        } catch (Exception e) {
            errors.add(new Error(e));
            errorProvider.printReport(errors);
        }
    }

    @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Riga")
    public void sendWeeklyNotification() {
        try {
            log.info("Scheduled job: Preparing statistics notifications");
            notificationService.prepareAndSendNotification();
        }  catch (Exception e) {
            errors.add(new Error(e));
            errorProvider.printReport(errors);
        }
    }



    private void updateStationData() {
        log.info("Starting scheduled job to update station data");
        for (GasStations gasStation : GasStations.getGasStationValues()) {
            final long gasStationId = gasStation.getId();
            final String url = gasStation.getUrl();
            final String gasStationTitle = gasStation.name().toLowerCase();
            final Modifier stationModifier = modifierFactory.createModifier(gasStationTitle);

            try {
                final Document document = isViada(gasStation) ? Jsoup.parse(getNoneSslHtml(gasStation.getUrl())) : Jsoup.connect(url).get();

                final Elements element = parsingWebSites(gasStation, document);
                log.info("pulling gas prices for {}", gasStationTitle);

                final List<GasTypeDto> gasTypesData = getGasTypesData(element, stationModifier, gasStationTitle);
                log.info("Truncating table {}", gasStation.getTitle());
                baseStationService.deleteTable(gasStationTitle);

                final List<BaseStation> stationList = gasTypesData.stream()
                        .map(data -> {
                            final BaseStation station = baseFactory.createStationInstance(gasStationTitle);
                            station.setGasType(stationModifier.adjustCorrectFieldTitleForDB(data.type()));
                            station.setPrice(getClearData(data.price()));
                            station.setLocation(getClearData(data.address()));
                            station.setGasStationsBrands(findById(gasStationId));
                            return station;
                        }).toList();

                stationList.forEach(station -> saveStationData(station, gasStationTitle));
            } catch (IOException | ParsingException | NoSuchElementException | NoSuchAlgorithmException |
                     KeyManagementException | InterruptedException e) {
                errors.add(new Error(e));
            }
        }

        if (!errors.isEmpty()) {
            errorProvider.printReport(errors);
        }
    }

    private void updatePriceHistory() {
        log.info("Starting scheduled job to update price history data");


        GasStations.getGasStationValues()
                .forEach((gasStation) -> {
                    final BaseHistoryRepository<BaseHistory> repository = baseHistoryService.getHistoryRepository(gasStation.getTitle());
                    final Optional<BaseHistory> history = repository.findTodayPrice(LocalDate.now());
                    final Set<BaseStation> listOfPricesFromOneStation = latestStationData.get(gasStation);
                    final BaseHistory baseHistory = history.orElse(baseFactory.createHistoryInstance(gasStation));

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

        GasStations.getGasStationValues()
                .forEach((gasStation) -> {
                    final BaseHistoryRepository<BaseHistory> repository = baseHistoryService.getHistoryRepository(gasStation.getTitle());
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

    private Elements parsingWebSites(final GasStations gasStations, final Document document) throws ParsingException
    {
        final Elements element = document.select(gasStations.getCssQuery());

        if (element.isEmpty()) {
            final String error = String.format("No data were found under cssQuery %s for gas station: %s", gasStations.getCssQuery(), gasStations.getTitle());
            throw new ParsingException(error);
        }
        return element;
    }


    private List<GasTypeDto> getGasTypesData(final Elements elements, final Modifier stationModifier, final String gasStation) throws ParsingException {
        final List<String> list = elements.stream().map(Element::text).collect(Collectors.toList());
        list.stream().findAny().orElseThrow(()-> new ParsingException("Jsoup elements are empty for " + gasStation));
        return stationModifier.getFullTypeData(list);
    }

    private String getClearData(final String inputData) {
      return inputData.replace("EUR","").replace("[.,]$", "").trim();
    }

    private boolean isViada(final GasStations gasStation) {
        return gasStation == GasStations.VIADA;
    }

    private String getNoneSslHtml(final String url) throws NoSuchAlgorithmException, KeyManagementException, IOException, InterruptedException {
        TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAll, new SecureRandom());

        final HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

}
