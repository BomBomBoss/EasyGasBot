package org.easybot.factory;

import org.easybot.CommonTexts;
import org.easybot.repository.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Component
public class RepositoryFactory {

    private final NesteRepository nesteRepository;
    private final CircleRepository circleRepository;
    private final ViadaRepository viadaRepository;
    private final VirsiRepository virsiRepository;
    private final Map<String, CommonStationRepository> repositoryMap = new HashMap<>();


    public RepositoryFactory(NesteRepository nesteRepository, CircleRepository circleRepository, ViadaRepository viadaRepository, VirsiRepository virsiRepository)
    {
        this.nesteRepository = nesteRepository;
        this.circleRepository = circleRepository;
        this.viadaRepository = viadaRepository;
        this.virsiRepository = virsiRepository;
        setRepositoryMap();
    }

    private void setRepositoryMap()
    {
        repositoryMap.put(CommonTexts.NESTE_TITLE, nesteRepository);
        repositoryMap.put(CommonTexts.CIRCLE_WITHOUT_K_TITLE, circleRepository);
        repositoryMap.put(CommonTexts.VIADA_TITLE, viadaRepository);
        repositoryMap.put(CommonTexts.VIRSI_TITLE, virsiRepository);
    }

    public Map<String, CommonStationRepository> getRepositoryMap()
    {
        return repositoryMap;
    }
}
