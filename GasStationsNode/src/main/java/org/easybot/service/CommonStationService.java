package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.easybot.entity.CommonStation;
import org.easybot.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CommonStationService {
    private final NesteRepository nesteRepository;
    private final CircleRepository circleRepository;
    private final ViadaRepository viadaRepository;
    private final VirsiRepository virsiRepository;

    public CommonStationService(NesteRepository nesteRepository, CircleRepository circleRepository, ViadaRepository viadaRepository, VirsiRepository virsiRepository)
    {
        this.nesteRepository = nesteRepository;
        this.circleRepository = circleRepository;
        this.viadaRepository = viadaRepository;
        this.virsiRepository = virsiRepository;
    }

    public void save(CommonStation station, String gasStationTitle)
    {
        returnCorrectRepository(gasStationTitle).save(station);
    }

    public void deleteTable(String gasStationTitle)
    {
        returnCorrectRepository(gasStationTitle).clearTable();
    }

    public List<CommonStation> retrieveAll(String tableTitle)
    {
      return returnCorrectRepository(tableTitle).findAll();

    }

    private CommonStationRepository returnCorrectRepository(String title)
    {
        if (title.equals("circle_k"))
            title = title.substring(0, title.length()-2);

        return switch (title)
                {
                    case "neste" -> nesteRepository;
                    case "circle" -> circleRepository;
                    case "viada" -> viadaRepository;
                    case "virsi" -> virsiRepository;

                    default -> throw new RuntimeException("Can't return repository instance");

                };

    }
}
