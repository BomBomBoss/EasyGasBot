package org.easybot.service.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.easybot.dto.Error;
import org.easybot.dto.IgnitisLocation;
import org.easybot.entity.charging.ChargingConnector;
import org.easybot.entity.charging.ChargingStation;
import org.easybot.repository.charging.ChargingStationRepository;
import org.easybot.service.ErrorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ChargingStationJobsService {

    private static final String SOURCE_URL = "https://ignitison.lv/ignitis-uzlades-stacijas/karte?_wrapper_format=drupal_ajax";
    private static final Pattern RIGA_PATTERN = Pattern.compile("\\bR[īi]ga\\b");
    private static final String LATVIA_MARKER = "Latvij";
    private static final Predicate<IgnitisLocation> IS_RIGA = location -> {
        final String address = location.address();
        return address != null && address.contains(LATVIA_MARKER) && RIGA_PATTERN.matcher(address).find();
    };

    private final ChargingStationRepository chargingStationRepository;
    private final ErrorProvider errorProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient;

    @Autowired
    public ChargingStationJobsService(final ChargingStationRepository chargingStationRepository,
                                      final ErrorProvider errorProvider,
                                      final RestClient chargingMapRestClient) {
        this.chargingStationRepository = chargingStationRepository;
        this.errorProvider = errorProvider;
        this.restClient = chargingMapRestClient;
    }

    @Scheduled(cron = "0 30 10 * * *", zone = "Europe/Riga")
    @Transactional
    public void syncChargingStations() {
        try {
            log.info("Starting scheduled job to sync Riga charging stations");
            final List<IgnitisLocation> rigaLocations = fetchLocations().stream()
                    .filter(IS_RIGA)
                    .toList();

            final Set<String> seenExternalIds = new HashSet<>();
            rigaLocations.forEach(location -> {
                upsertStation(location);
                seenExternalIds.add(location.id());
            });

            chargingStationRepository.findByActiveTrue().stream()
                    .filter(station -> !seenExternalIds.contains(station.getExternalId()))
                    .forEach(station -> {
                        station.setActive(false);
                        chargingStationRepository.save(station);
                    });

            log.info("Synced {} Riga charging stations", rigaLocations.size());
        } catch (Exception e) {
            final List<Error> errors = new ArrayList<>();
            errors.add(new Error(e));
            errorProvider.printGeneralReport(errors);
        }
    }

    private void upsertStation(final IgnitisLocation location) {
        final ChargingStation station = chargingStationRepository.findByExternalId(location.id())
                .orElseGet(ChargingStation::new);

        station.setExternalId(location.id());
        station.setName(location.label());
        station.setAddress(location.address());
        station.setDistrict(extractDistrict(location.address()));
        station.setLatitude(parseCoordinate(location.latlng(), 0));
        station.setLongitude(parseCoordinate(location.latlng(), 1));
        station.setStatus(location.status());
        station.setPartner(location.isPartner());
        station.setAlwaysOpen(location.isAlwaysOpen());
        station.setAvailability(location.availability() != null && location.availability().isTextual()
                ? location.availability().asText()
                : null);
        station.setActive(true);
        station.setLastSyncedAt(LocalDateTime.now());

        station.getConnectors().clear();
        location.connectorsGrouped().forEach(group -> {
            final ChargingConnector connector = new ChargingConnector();
            connector.setChargingStation(station);
            connector.setConnectorType(group.type());
            connector.setTypeLabel(group.typeLabel());
            connector.setPowerKw(group.power());
            connector.setPricePerKwh(group.price());
            connector.setConnectorCount(group.count());
            station.getConnectors().add(connector);
        });

        chargingStationRepository.save(station);
    }

    private String extractDistrict(final String address) {
        for (String part : address.split(",")) {
            final String trimmed = part.trim();
            if (trimmed.endsWith("priekšpilsēta") || trimmed.endsWith("rajons")) {
                return trimmed;
            }
        }
        return null;
    }

    private BigDecimal parseCoordinate(final List<String> latlng, final int index) {
        if (latlng == null || latlng.size() <= index) {
            return null;
        }
        return new BigDecimal(latlng.get(index));
    }

    private List<IgnitisLocation> fetchLocations() throws JsonProcessingException {
        final String body = restClient.get()
                .uri(SOURCE_URL)
                .retrieve()
                .body(String.class);

        final JsonNode commands = objectMapper.readTree(body);

        for (JsonNode command : commands) {
            if ("settings".equals(command.path("command").asText())) {
                final JsonNode locations = command.path("settings").path("ignitisChargingMap").path("locations");
                if (!locations.isMissingNode()) {
                    return objectMapper.convertValue(locations, new TypeReference<>() {});
                }
            }
        }

        throw new IllegalStateException("Could not find ignitisChargingMap.locations in charging map response");
    }

}
