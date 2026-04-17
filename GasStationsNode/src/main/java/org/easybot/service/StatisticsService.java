package org.easybot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.easybot.CommonTexts.CIRCLE_K_TITLE;
import org.easybot.entity.history.BaseHistory;
import org.easybot.factory.RepositoryFactory;
import org.easybot.repository.history.BaseHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {

    private final RepositoryFactory repositoryFactory;


    private BaseHistoryRepository getRepositoryInstance(String title) {
        if (title.equals(CIRCLE_K_TITLE))
            title = title.substring(0, title.length() - 2);

        BaseHistoryRepository repository = repositoryFactory.getHistoryRepositoryMap().get(title);

        if (repository == null) {
            throw new RuntimeException("Can't return repository instance");
        }

        return repository;
    }


    public Map<String, BaseHistoryRepository<? extends BaseHistory>> getHistoryRepositoryMap() {
        return repositoryFactory.getHistoryRepositoryMap();
    }
}
