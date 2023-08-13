package org.easybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GasStationApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(GasStationApplication.class);
    }
}
