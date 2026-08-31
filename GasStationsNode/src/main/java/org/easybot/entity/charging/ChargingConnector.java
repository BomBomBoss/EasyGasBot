package org.easybot.entity.charging;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "charging_connector")
@Getter
@Setter
public class ChargingConnector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "charging_station_id", referencedColumnName = "id")
    private ChargingStation chargingStation;

    private String connectorType;
    private String typeLabel;
    private BigDecimal powerKw;
    private BigDecimal pricePerKwh;
    private int connectorCount;

}
