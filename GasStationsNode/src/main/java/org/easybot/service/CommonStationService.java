package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.easybot.entity.CommonStation;
import org.easybot.factory.RepositoryFactory;
import org.easybot.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.easybot.CommonTexts.*;

@Service
@Slf4j
public class CommonStationService {
   private final RepositoryFactory repositoryFactory;

    public CommonStationService(RepositoryFactory repositoryFactory)
    {
        this.repositoryFactory = repositoryFactory;
    }


    public void save(CommonStation station, String gasStationTitle)
    {
        getRepositoryInstance(gasStationTitle).save(station);
    }

    public void deleteTable(String gasStationTitle)
    {
        getRepositoryInstance(gasStationTitle).clearTable();
    }

    public List<CommonStation> retrieveAll(String tableTitle)
    {
      return getRepositoryInstance(tableTitle).findAll();

    }

    private CommonStationRepository getRepositoryInstance(String title)
    {
        if (title.equals(CIRCLE_K_TITLE))
            title = title.substring(0, title.length()-2);

        CommonStationRepository repository = repositoryFactory.getRepositoryMap().get(title);

        if(repository==null)
        {
            throw new RuntimeException ("Can't return repository instance");
        }

        return repository;

    }
}
