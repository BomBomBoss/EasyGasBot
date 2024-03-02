package org.easybot.configuration;

import jakarta.annotation.PostConstruct;
import org.easybot.entity.GasStationsBrands;
import org.easybot.enums.GasStationTitle;
import org.easybot.service.GasStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
public class CommonConfiguration {
    private final GasStationService gasStationService;


    @Autowired
    public CommonConfiguration(GasStationService gasStationService)
    {
        this.gasStationService = gasStationService;
    }

    @PostConstruct
    public void setUpCorrectId()
    {
        List< GasStationsBrands> brandsList = gasStationService.findAllBrands();

        for (GasStationTitle gs : GasStationTitle.values())
        {
           GasStationsBrands brands = brandsList.stream().filter(x-> gs.getTitle()
                   .equalsIgnoreCase(x.getBrandName()))
                   .findFirst()
                   .orElseThrow(() -> new RuntimeException(gs.getTitle() + "can't be found in DB"));
           gs.setId(brands.getId());
        }
    }
}
