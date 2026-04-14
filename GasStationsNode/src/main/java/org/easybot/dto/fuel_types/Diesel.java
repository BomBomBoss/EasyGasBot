package org.easybot.dto.fuel_types;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Diesel {

    private Long stationBrandId;
    private String price;
    private LocalDate date;

}
