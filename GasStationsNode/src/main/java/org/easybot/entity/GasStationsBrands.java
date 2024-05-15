package org.easybot.entity;

import jakarta.persistence.*;
import lombok.Getter;

import static org.easybot.CommonTexts.CIRCLE_K_TITLE;
import static org.easybot.CommonTexts.CIRCLE_WITHOUT_K_TITLE;

@Entity
@Table
@Getter
public class GasStationsBrands {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brandName;

    public String getFormattedBrandName()
    {
        return brandName.equals(CIRCLE_K_TITLE) ? CIRCLE_WITHOUT_K_TITLE : brandName;
    }

}
