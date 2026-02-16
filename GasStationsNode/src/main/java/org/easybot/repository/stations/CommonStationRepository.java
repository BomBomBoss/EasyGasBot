package org.easybot.repository.stations;

import jakarta.transaction.Transactional;
import org.easybot.entity.stations.BaseStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;

@NoRepositoryBean
public interface CommonStationRepository<T extends BaseStation> extends JpaRepository <T, Long> {

    @Query(value = "delete from #{#entityName} t")
    @Modifying
    @Transactional
    void clearTable();


    @Query(value = "select t from #{#entityName} t")
    @Transactional
    List<T> findAll();

    @Query(value = "select t from #{#entityName} t WHERE t.gasType = :type")
    @Transactional
    T findByType(@Param("type") String type);


}
