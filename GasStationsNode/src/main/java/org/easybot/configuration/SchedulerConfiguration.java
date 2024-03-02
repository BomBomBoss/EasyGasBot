package org.easybot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
@Configuration
@EnableScheduling
@Profile("!test")
public class SchedulerConfiguration {
}
