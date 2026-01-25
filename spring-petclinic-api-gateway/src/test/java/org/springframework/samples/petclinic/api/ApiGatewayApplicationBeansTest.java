package org.springframework.samples.petclinic.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApiGatewayApplicationBeansTest {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    WebClient.Builder webClientBuilder;
    @Autowired
    RouterFunction<?> routerFunction;
    @Autowired
    Customizer<?> defaultCustomizer;

    @Test
    void beans_areCreated() {
        assertThat(restTemplate).isNotNull();
        assertThat(webClientBuilder).isNotNull();
        assertThat(routerFunction).isNotNull();
        assertThat(defaultCustomizer).isNotNull();
    }
}
