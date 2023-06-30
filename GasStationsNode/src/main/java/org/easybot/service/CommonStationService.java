package org.easybot.service;

import org.easybot.repository.CommonStationRepository;

public class CommonStationService {
    private final CommonStationRepository commonStationRepository;

    public CommonStationService(CommonStationRepository commonStationRepository)
    {
        this.commonStationRepository = commonStationRepository;
    }
}
