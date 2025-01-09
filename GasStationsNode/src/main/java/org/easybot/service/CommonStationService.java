package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import static org.easybot.CommonTexts.CIRCLE_K_TITLE;
import org.easybot.entity.stations.CommonStation;
import org.easybot.factory.RepositoryFactory;
import org.easybot.repository.stations.CommonStationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CommonStationService {
    private final RepositoryFactory repositoryFactory;

    public CommonStationService(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }


    public CommonStation save(CommonStation station, String gasStationTitle) {
        return (CommonStation) getRepositoryInstance(gasStationTitle).save(station);
    }

    public void deleteTable(String gasStationTitle) {
        getRepositoryInstance(gasStationTitle).clearTable();
    }

    public List<CommonStation> retrieveAll(String tableTitle) {
        return getRepositoryInstance(tableTitle).findAll();
    }

    public CommonStation retrieveStationByType(String tableTitle, String type) {
        return getRepositoryInstance(tableTitle).findByType(type);
    }

    private CommonStationRepository getRepositoryInstance(String title) {
        if (title.equals(CIRCLE_K_TITLE))
            title = title.substring(0, title.length() - 2);

        CommonStationRepository repository = repositoryFactory.getStationRepositoryMap().get(title);

        if (repository == null) {
            throw new RuntimeException("Can't return repository instance");
        }

        return repository;
    }
}
