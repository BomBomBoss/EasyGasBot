package org.easybot.factory;

import lombok.Getter;
import org.easybot.CommonTexts;
import org.easybot.enums.GasStationTitle;
import static org.easybot.enums.GasStationTitle.CIRCLE;
import static org.easybot.enums.GasStationTitle.NESTE;
import static org.easybot.enums.GasStationTitle.VIADA;
import static org.easybot.enums.GasStationTitle.VIRSI;
import org.easybot.repository.history.CircleHistoryRepository;
import org.easybot.repository.history.CommonHistoryRepository;
import org.easybot.repository.history.NesteHistoryRepository;
import org.easybot.repository.history.ViadaHistoryRepository;
import org.easybot.repository.history.VirsiHistoryRepository;
import org.easybot.repository.stations.CircleRepository;
import org.easybot.repository.stations.CommonStationRepository;
import org.easybot.repository.stations.NesteRepository;
import org.easybot.repository.stations.ViadaRepository;
import org.easybot.repository.stations.VirsiRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RepositoryFactory {

    private final NesteRepository nesteRepository;
    private final CircleRepository circleRepository;
    private final ViadaRepository viadaRepository;
    private final VirsiRepository virsiRepository;

    private final NesteHistoryRepository nesteHistoryRepository;
    private final CircleHistoryRepository circleHistoryRepository;
    private final ViadaHistoryRepository viadaHistoryRepository;
    private final VirsiHistoryRepository virsiHistoryRepository;

    @Getter
    private final Map<String, CommonStationRepository> stationRepositoryMap = new HashMap<>();
    @Getter
    private final Map<GasStationTitle, CommonHistoryRepository> historyRepositoryMap = new HashMap<>();


    public RepositoryFactory(NesteRepository nesteRepository, CircleRepository circleRepository, ViadaRepository viadaRepository, VirsiRepository virsiRepository, NesteHistoryRepository nesteHistoryRepository, CircleHistoryRepository circleHistoryRepository, ViadaHistoryRepository viadaHistoryRepository, VirsiHistoryRepository virsiHistoryRepository)
    {
        this.nesteRepository = nesteRepository;
        this.circleRepository = circleRepository;
        this.viadaRepository = viadaRepository;
        this.virsiRepository = virsiRepository;
        this.nesteHistoryRepository = nesteHistoryRepository;
        this.circleHistoryRepository = circleHistoryRepository;
        this.viadaHistoryRepository = viadaHistoryRepository;
        this.virsiHistoryRepository = virsiHistoryRepository;
        setStationRepositoryMap();
        setStationHistoryRepositoryMap();
    }

    private void setStationRepositoryMap()
    {
        stationRepositoryMap.put(CommonTexts.NESTE_TITLE, nesteRepository);
        stationRepositoryMap.put(CommonTexts.CIRCLE_WITHOUT_K_TITLE, circleRepository);
        stationRepositoryMap.put(CommonTexts.VIADA_TITLE, viadaRepository);
        stationRepositoryMap.put(CommonTexts.VIRSI_TITLE, virsiRepository);
    }

    private void setStationHistoryRepositoryMap()
    {
        historyRepositoryMap.put(NESTE, nesteHistoryRepository);
        historyRepositoryMap.put(CIRCLE, circleHistoryRepository);
        historyRepositoryMap.put(VIADA, viadaHistoryRepository);
        historyRepositoryMap.put(VIRSI, virsiHistoryRepository);
    }

}
