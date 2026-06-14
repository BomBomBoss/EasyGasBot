package org.easybot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easybot.dto.fuel_types.FuelHistoryData;
import org.easybot.entity.cheapest_price.CheapestHistoryPrice;
import org.easybot.entity.enums.GasTypesName;
import org.easybot.entity.history.BaseHistory;
import org.easybot.enums.GasStations;
import org.easybot.factory.RepositoryFactory;
import org.easybot.repository.cheapest_price.CheapestHistoryPriceRepository;
import org.easybot.repository.history.BaseHistoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseHistoryService {

    private final RepositoryFactory repositoryFactory;
    private final CheapestHistoryPriceRepository cheapestHistoryPriceRepository;


    public BaseHistoryRepository<BaseHistory> getHistoryRepository(final String title) {
        return repositoryFactory.getHistoryRepository(title);
    }

    public List<CheapestHistoryPrice> getHistoryPriceByRange(final Integer daysRange) {
        final LocalDate startDate = LocalDate.now().minusDays(daysRange.longValue());
        return cheapestHistoryPriceRepository.findByDateAfterOrderByDate(startDate);
    }

    public void updateCheapestHistoryPrice() {
        final List<BaseHistory> allStationsHistory = new ArrayList<>();
        GasStations.getGasStationValues()
                .forEach(station -> {
                    final BaseHistoryRepository<BaseHistory> repository = getHistoryRepository(station.getTitle());
                    allStationsHistory.addAll(repository.findAll());
                });
        final Map<LocalDate, List<BaseHistory>> allStationsHistoryMap = allStationsHistory.stream()
                .collect(Collectors.groupingBy(BaseHistory::getDate));

        cheapestHistoryPriceRepository.deleteAll();
        GasTypesName.getGeneralTypes().forEach(type -> updateCheapestGasType(type, allStationsHistoryMap));
    }

    private void updateCheapestGasType(final GasTypesName gasType, final Map<LocalDate, List<BaseHistory>> allStationsHistoryMap) {
        final Map<LocalDate, FuelHistoryData> cheapestFuelType = allStationsHistoryMap.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                        .min(Comparator.comparing(station -> getPrice(station, gasType)))
                        .map(station -> FuelHistoryData.builder()
                                .brandId(station.getGasStationsBrands().getId())
                                .type(gasType)
                                .price(getPrice(station, gasType).setScale(3, RoundingMode.HALF_UP).toString())
                                .build()).orElse(FuelHistoryData.builder().type(gasType).build())
        ));

        final List<CheapestHistoryPrice> cheapestHistoryPrices = cheapestFuelType.entrySet().stream()
                .map(entry -> {
                    final CheapestHistoryPrice historyPrice = new CheapestHistoryPrice();
                    historyPrice.setBrandId(entry.getValue().brandId());
                    historyPrice.setGasTypeId(entry.getValue().type().getId());
                    historyPrice.setPrice(entry.getValue().price());
                    historyPrice.setDate(entry.getKey());
                    return historyPrice;
                }).toList();

        cheapestHistoryPriceRepository.saveAll(cheapestHistoryPrices);
    }

    private BigDecimal getPrice(final BaseHistory station, final GasTypesName gasType) {
        log.info("Calculating price for gas type {}, station {}, date {}", gasType.getDescription(), station.getGasStationsBrands().getBrandName(), station.getDate());
        return switch (gasType.getId().intValue()) {
            case 1 -> new BigDecimal(station.getPrice_95E().trim());
            case 2 -> new BigDecimal(station.getPrice_98E().trim());
            case 3 -> new BigDecimal(station.getPrice_diesel().trim());
            default -> throw new IllegalArgumentException("Unknown gas type");
        };
    }
}
