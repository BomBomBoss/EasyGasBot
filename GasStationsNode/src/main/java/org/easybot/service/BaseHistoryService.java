package org.easybot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easybot.entity.history.BaseHistory;
import org.easybot.factory.RepositoryFactory;
import org.easybot.repository.history.BaseHistoryRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseHistoryService {

    private final RepositoryFactory repositoryFactory;


    public BaseHistoryRepository<BaseHistory> getHistoryRepository(final String title) {
        return repositoryFactory.getHistoryRepository(title);
    }
}
