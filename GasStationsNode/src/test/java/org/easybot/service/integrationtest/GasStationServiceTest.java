package org.easybot.service.integrationtest;

import org.easybot.entity.*;
import org.easybot.repository.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GasStationServiceTest {

    @Autowired
    GasStationsRepository gasStationsRepository;
    @Autowired
    NesteRepository nesteRepository;
    @Autowired
    CircleRepository circleRepository;
    @Autowired
    ViadaRepository viadaRepository;
    @Autowired
    VirsiRepository virsiRepository;


    @ServiceConnection
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.1")
            .withInitScript("db/init.sql");

    @Test
    void contextLoads()
    {
        System.out.println(postgreSQLContainer.getLogs());
    }

    @Test
    void checkIfBrandTableHasCorrectAmountOfRows()
    {
        Assertions.assertEquals(4, gasStationsRepository.findAll().size());
    }

    @Test
    void checkIfNesteHasCorrectPrice_valid()
    {
        List<Neste> list = nesteRepository.findAll();
        Neste type95E = list.stream().filter(type -> type.getGasType().equals("95E")).findFirst().orElse(null);
        Assertions.assertNotNull(type95E);
        Assertions.assertEquals("1.10", type95E.getPrice());
    }

    @Test
    void checkIViadaHasCorrectData_valid()
    {
        List<Viada> list = viadaRepository.findAll();
        Viada diesel = list.stream().filter(type -> type.getGasType().equals("Diesel")).findFirst().orElse(null);
        Assertions.assertNotNull(diesel);
        Assertions.assertEquals("Daugavpils", diesel.getLocation());
    }

    @Test
    void checkIfCheapestPriceSortingIsCorrect_valid()
    {
        List<CommonStation> list = priceSorting();

        CommonStation first = list.get(0);
        CommonStation second = list.get(1);
        CommonStation third = list.get(2);
        CommonStation fourth = list.get(3);


        Assertions.assertInstanceOf(Neste.class, first);
        Assertions.assertInstanceOf(CircleK.class, second);
        Assertions.assertInstanceOf(Viada.class, third);
        Assertions.assertInstanceOf(Virsi.class, fourth);
    }

    @Test
    void checkIfCheapestPriceSortingIsCorrect_Invalid()
    {
        List<CommonStation> list = priceSorting();

        CommonStation first = list.getFirst();

        Assertions.assertFalse(first instanceof CircleK);
    }

    @NotNull
    private List<CommonStation> priceSorting()
    {
        String type98E = "98E";
        List<CommonStation> list = new ArrayList<>(4);
        list.add(viadaRepository.findByType(type98E));
        list.add(nesteRepository.findByType(type98E));
        list.add(virsiRepository.findByType(type98E));
        list.add(circleRepository.findByType(type98E));
        Collections.sort(list);
        return list;
    }

}