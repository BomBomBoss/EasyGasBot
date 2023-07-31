package org.easybot.service;

import org.easybot.entity.CommonStation;
import org.easybot.repository.CommonStationRepository;
import org.springframework.stereotype.Service;

@Service
public class CommonStationService <T extends CommonStation> {
    private final CommonStationRepository <T> commonStationRepository;

    public CommonStationService(CommonStationRepository commonStationRepository)
    {
        this.commonStationRepository = commonStationRepository;
    }

    public void save (T station)
    {
        commonStationRepository.save(station);
    }
}
