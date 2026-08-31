package org.easybot.service;

import lombok.RequiredArgsConstructor;
import org.easybot.dto.ChargingDistrictSummary;
import org.easybot.dto.StationConnectorPair;
import org.easybot.entity.charging.ChargingStation;
import org.easybot.repository.charging.ChargingStationRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class ChargingStationService {

    private static final String IN_OPERATION_STATUS = "in_operation";
    private static final int TOP_STATIONS_PER_DISTRICT = 3;
    private static final List<String> NON_PUBLIC_NAME_MARKERS = List.of("nav publiska", "not public");

    private static final Predicate<ChargingStation> IS_PUBLICLY_ACCESSIBLE = station -> {
        final String name = station.getName();
        if (name == null) {
            return true;
        }
        final String lowerCaseName = name.toLowerCase(Locale.ROOT);
        return NON_PUBLIC_NAME_MARKERS.stream().noneMatch(lowerCaseName::contains);
    };

    private final ChargingStationRepository chargingStationRepository;

    public List<ChargingDistrictSummary> findCheapestPerDistrict() {
        final List<ChargingStation> stations = chargingStationRepository.findActiveWithConnectors(IN_OPERATION_STATUS).stream()
                .filter(IS_PUBLICLY_ACCESSIBLE)
                .toList();

        final List<StationConnectorPair> pairs = stations.stream()
                .flatMap(station -> station.getConnectors().stream()
                        .filter(connector -> connector.getPricePerKwh() != null)
                        .map(connector -> new StationConnectorPair(station, connector)))
                .toList();

        final List<String> districts = pairs.stream()
                .map(pair -> pair.station().getDistrict())
                .distinct()
                .sorted(Comparator.nullsLast(Comparator.naturalOrder()))
                .toList();

        return districts.stream()
                .flatMap(district -> pairs.stream()
                        .filter(pair -> Objects.equals(pair.station().getDistrict(), district))
                        .sorted(Comparator.comparing(pair -> pair.connector().getPricePerKwh()))
                        .limit(TOP_STATIONS_PER_DISTRICT)
                        .map(this::toSummary))
                .toList();
    }

    private ChargingDistrictSummary toSummary(final StationConnectorPair pair) {
        return ChargingDistrictSummary.builder()
                .district(pair.station().getDistrict())
                .stationName(pair.station().getName())
                .address(pair.station().getAddress())
                .connectorTypeLabel(pair.connector().getTypeLabel())
                .powerKw(pair.connector().getPowerKw())
                .pricePerKwh(pair.connector().getPricePerKwh())
                .build();
    }

}
