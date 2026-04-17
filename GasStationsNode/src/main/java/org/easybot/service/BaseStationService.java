package org.easybot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.easybot.CommonTexts.CIRCLE_K_TITLE;
import static org.easybot.CommonTexts.CIRCLE_WITHOUT_K_TITLE;
import org.easybot.entity.stations.BaseStation;
import org.easybot.factory.RepositoryFactory;
import org.easybot.repository.stations.CommonStationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseStationService {

    private final RepositoryFactory repositoryFactory;


    @Transactional
    public BaseStation save(final BaseStation station, final String gasStationTitle) {
        return getRepositoryInstance(gasStationTitle).save(station);
    }

    public void deleteTable(final String gasStationTitle) {
        getRepositoryInstance(gasStationTitle).clearTable();
    }

    public List<BaseStation> retrieveAll(final String tableTitle) {
        return getRepositoryInstance(tableTitle).findAll();
    }

    public BaseStation retrieveStationByType(final String tableTitle, final String type) {
        return getRepositoryInstance(tableTitle).findByType(type);
    }

    private CommonStationRepository<BaseStation> getRepositoryInstance(final String title) {
        final String tableName = title.equals(CIRCLE_K_TITLE) ? CIRCLE_WITHOUT_K_TITLE : title;
        return repositoryFactory.getStationRepository(tableName);
    }
}
