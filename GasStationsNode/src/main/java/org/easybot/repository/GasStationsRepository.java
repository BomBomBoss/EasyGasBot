package org.easybot.repository;

import org.easybot.entity.GasStationsBrands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GasStationsRepository extends JpaRepository<GasStationsBrands, Long> {
}
