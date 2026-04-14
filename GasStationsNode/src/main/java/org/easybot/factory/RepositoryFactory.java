package org.easybot.factory;

import lombok.Getter;
import org.easybot.CommonTexts;
import org.easybot.entity.history.BaseHistory;
import org.easybot.entity.stations.BaseStation;
import static org.easybot.enums.GasStations.CIRCLE;
import static org.easybot.enums.GasStations.NESTE;
import static org.easybot.enums.GasStations.VIADA;
import static org.easybot.enums.GasStations.VIRSI;
import org.easybot.repository.history.BaseHistoryRepository;
import org.easybot.repository.history.CircleHistoryRepository;
import org.easybot.repository.history.NesteHistoryRepository;
import org.easybot.repository.history.ViadaHistoryRepository;
import org.easybot.repository.history.VirsiHistoryRepository;
import org.easybot.repository.stations.CircleRepository;
import org.easybot.repository.stations.CommonStationRepository;
import org.easybot.repository.stations.NesteRepository;
import org.easybot.repository.stations.ViadaRepository;
import org.easybot.repository.stations.VirsiRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RepositoryFactory {

    @Getter
    private final Map<String, CommonStationRepository<? extends BaseStation>> stationRepositoryMap;
    @Getter
    private final Map<String, BaseHistoryRepository<? extends BaseHistory>> historyRepositoryMap;


    public RepositoryFactory(final NesteRepository nesteRepository, final CircleRepository circleRepository,
                             final ViadaRepository viadaRepository, final VirsiRepository virsiRepository,
                             final NesteHistoryRepository nesteHistoryRepository, final CircleHistoryRepository circleHistoryRepository,
                             final ViadaHistoryRepository viadaHistoryRepository, final VirsiHistoryRepository virsiHistoryRepository) {
        stationRepositoryMap = Map.of(
                CommonTexts.NESTE_TITLE, nesteRepository,
                CommonTexts.CIRCLE_WITHOUT_K_TITLE, circleRepository,
                CommonTexts.VIADA_TITLE, viadaRepository,
                CommonTexts.VIRSI_TITLE, virsiRepository);

        historyRepositoryMap = Map.of(
                NESTE.getTitle(), nesteHistoryRepository,
                CIRCLE.getTitle(), circleHistoryRepository,
                VIADA.getTitle(), viadaHistoryRepository,
                VIRSI.getTitle(), virsiHistoryRepository);
    }



    @SuppressWarnings("unchecked")
    public <T extends BaseStation> CommonStationRepository<T> getStationRepository(final String station) {
        CommonStationRepository<? extends BaseStation> repo = stationRepositoryMap.get(station);
        if (repo == null) {
            throw new RuntimeException("No station repository for " + station);
        }
        return (CommonStationRepository<T>) repo;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseHistory> BaseHistoryRepository<T> getHistoryRepository(final String station) {
        BaseHistoryRepository<? extends BaseHistory> repo = historyRepositoryMap.get(station);
        if (repo == null) {
            throw new RuntimeException("No history repository for " + station);
        }
        return (BaseHistoryRepository<T>) repo;
    }

}
