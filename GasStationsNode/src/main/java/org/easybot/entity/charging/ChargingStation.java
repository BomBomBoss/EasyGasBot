package org.easybot.entity.charging;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "charging_station")
@Getter
@Setter
public class ChargingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalId;
    private String name;
    private String address;
    private String district;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String status;

    @Column(name = "is_partner")
    private boolean partner;

    @Column(name = "is_always_open")
    private boolean alwaysOpen;

    private String availability;

    @Column(name = "is_active")
    private boolean active;

    private LocalDateTime lastSyncedAt;

    @OneToMany(mappedBy = "chargingStation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargingConnector> connectors = new ArrayList<>();

}
