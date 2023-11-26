package org.easybot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static org.easybot.CommonTexts.CIRCLE_K_TITLE;
import static org.easybot.CommonTexts.CIRCLE_WITHOUT_K_TITLE;

@Entity
@Table
public class GasStationsBrands {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brandName;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getBrandName()
    {
        return brandName;
    }

    public String getFormattedBrandName()
    {
        return brandName.equals(CIRCLE_K_TITLE) ? CIRCLE_WITHOUT_K_TITLE : brandName;
    }

    public void setBrandName(String title)
    {
        this.brandName = title;
    }
}
