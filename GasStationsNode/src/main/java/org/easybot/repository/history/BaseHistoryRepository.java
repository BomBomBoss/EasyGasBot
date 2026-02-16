package org.easybot.repository.history;

import jakarta.transaction.Transactional;
import org.easybot.entity.history.BaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

@NoRepositoryBean
public interface BaseHistoryRepository<T extends BaseHistory> extends JpaRepository<T, Long> {

    @Query(value = "select t from #{#entityName} t WHERE t.date = :today")
    @Transactional
    Optional <T> findTodayPrice(@Param("today") LocalDate today);

    @Query(value = "select count(*) from #{#entityName}")
    int findRowsCount();

    @Modifying
    @Query(value = "delete from #{#entityName} t where t.date <= :threshold")
    @Transactional
    void deleteRedundantRows(@Param("threshold") LocalDate threshold);
}
