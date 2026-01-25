package org.springframework.samples.petclinic.customers.service.visit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class VisitsServiceClientTest {

    @Test
    void getVisitsForPets_returnsVisits() {
        Visits visits = new Visits(java.util.List.of(new VisitDetails()));
        WebClient.Builder builder = WebClient.builder().exchangeFunction(request -> {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String json = mapper.writeValueAsString(visits);
                org.springframework.core.io.buffer.DataBuffer buffer =
                    new org.springframework.core.io.buffer.DefaultDataBufferFactory()
                        .wrap(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                org.springframework.web.reactive.function.client.ClientResponse response =
                    org.springframework.web.reactive.function.client.ClientResponse
                        .create(org.springframework.http.HttpStatus.OK, org.springframework.web.reactive.function.client.ExchangeStrategies.withDefaults())
                        .header("Content-Type", org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
                        .body(reactor.core.publisher.Flux.just(buffer))
                        .build();
                return reactor.core.publisher.Mono.just(response);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                return reactor.core.publisher.Mono.error(e);
            }
        });
        VisitsServiceClient client = new VisitsServiceClient(builder);
        Visits result = client.getVisitsForPets(java.util.List.of(1, 2)).block();
        Assertions.assertNotNull(result);
        assertThat(result.getItems()).hasSize(1);
    }
}
