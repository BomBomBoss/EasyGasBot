package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import static org.easybot.CommonTexts.CIRCLE_K_TITLE;
import org.easybot.enums.GasStations;
import org.easybot.factory.RepositoryFactory;
import org.easybot.repository.history.BaseHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class StatisticsService {

    private final RepositoryFactory repositoryFactory;

    public StatisticsService(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    private BaseHistoryRepository getRepositoryInstance(String title) {
        if (title.equals(CIRCLE_K_TITLE))
            title = title.substring(0, title.length() - 2);

        BaseHistoryRepository repository = repositoryFactory.getHistoryRepositoryMap().get(title);

        if (repository == null) {
            throw new RuntimeException("Can't return repository instance");
        }

        return repository;
    }


    public Map<GasStations, BaseHistoryRepository> getHistoryRepositoryMap() {
        return repositoryFactory.getHistoryRepositoryMap();
    }
}
