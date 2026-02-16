package org.easybot.entity.history;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.easybot.entity.GasStationsBrands;

import java.time.LocalDate;

@MappedSuperclass
@Setter
@Getter
@ToString
@EqualsAndHashCode(exclude = "id")
public abstract class BaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "type_95E_price")
    private String price_95E;

    @Column(name = "type_98E_price")
    private String price_98E;

    @Column(name = "type_DIESEL_price")
    private String price_diesel;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    protected GasStationsBrands gasStationsBrands;
}
