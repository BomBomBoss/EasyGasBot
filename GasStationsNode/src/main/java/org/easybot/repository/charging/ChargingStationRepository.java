package org.easybot.repository.charging;

import org.easybot.entity.charging.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {

    Optional<ChargingStation> findByExternalId(String externalId);

    @Query("select distinct cs from ChargingStation cs left join fetch cs.connectors where cs.active = true and cs.status = :status")
    List<ChargingStation> findActiveWithConnectors(@Param("status") String status);

    List<ChargingStation> findByActiveTrue();

}
