package org.springframework.samples.petclinic.api.boundary.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;

@WebFluxTest(controllers = FallbackController.class)
class FallbackControllerTest {
    @Autowired
    private WebTestClient client;

    @Test
    void fallback_returns503() {
        client.post()
            .uri("/fallback")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody(String.class).isEqualTo("Chat is currently unavailable. Please try again later.");
    }
}
