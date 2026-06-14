package org.easybot.entity.cheapest_price;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "cheapest_history_price")
public class CheapestHistoryPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private Long brandId;

    @Column
    private Long gasTypeId;

    @Column
    private LocalDate date;

    @Column
    private String price;

}
