package org.easybot.service;

import org.easybot.entity.GasStationsBrands;
import org.easybot.repository.GasStationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GasStationService {

    private final GasStationsRepository gasStationsRepository;

    @Autowired
    public GasStationService(GasStationsRepository gasStationsRepository)
    {
        this.gasStationsRepository = gasStationsRepository;
    }

    public List<GasStationsBrands> findAllBrands()
    {
        return gasStationsRepository.findAll();
    }
    public GasStationsBrands findById(Long id)
    {
        return gasStationsRepository.findById(id).orElseThrow(()->new RuntimeException("Can't find this {" + id + "} in table"));
    }
}
