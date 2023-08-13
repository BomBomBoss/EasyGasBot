package org.easybot.repository;

import jakarta.transaction.Transactional;
import org.easybot.entity.CircleK;
import org.easybot.entity.CommonStation;
import org.easybot.entity.Neste;
import org.easybot.entity.Viada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface CommonStationRepository<T extends CommonStation> extends JpaRepository <T, Long> {

    @Query(value = "delete from #{#entityName} t" )
    @Modifying
    @Transactional
    void clearTable();


    @Query(value = "select t from #{#entityName} t")
    List<T> findAll();


}
