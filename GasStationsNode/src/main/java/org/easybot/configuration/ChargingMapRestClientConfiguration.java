package org.easybot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class ChargingMapRestClientConfiguration {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(15);

    @Bean
    public RestClient chargingMapRestClient(final RestClient.Builder restClientBuilder) {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) CONNECT_TIMEOUT.toMillis());
        requestFactory.setReadTimeout((int) READ_TIMEOUT.toMillis());

        return restClientBuilder
                .requestFactory(requestFactory)
                // ignitison.lv only returns the charging map data for this Drupal AJAX
                // "shape" of request; both headers are required on every call.
                .defaultHeader("X-Requested-With", "XMLHttpRequest")
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
