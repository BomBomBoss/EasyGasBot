package org.easybot.repository;

import org.easybot.entity.CommonStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CommonStationRepository<T extends CommonStation> extends JpaRepository <T, Long> {
}
