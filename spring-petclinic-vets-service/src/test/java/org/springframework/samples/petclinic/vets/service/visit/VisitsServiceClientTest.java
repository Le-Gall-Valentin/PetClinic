package org.springframework.samples.petclinic.vets.service.visit;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class VisitsServiceClientTest {
    @Test
    void getVisitsByVetFrom_returnsList() {
        java.util.List<VisitsServiceClient.VisitDTO> payload = java.util.List.of(new VisitsServiceClient.VisitDTO(1,1,7,"2026-01-01","desc"));
        WebClient.Builder builder = WebClient.builder().exchangeFunction(request -> {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String json = mapper.writeValueAsString(payload);
                org.springframework.core.io.buffer.DataBuffer buffer =
                    new org.springframework.core.io.buffer.DefaultDataBufferFactory()
                        .wrap(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                org.springframework.web.reactive.function.client.ClientResponse response =
                    org.springframework.web.reactive.function.client.ClientResponse
                        .create(org.springframework.http.HttpStatus.OK, org.springframework.web.reactive.function.client.ExchangeStrategies.withDefaults())
                        .header("Content-Type", org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
                        .body(reactor.core.publisher.Flux.just(buffer))
                        .build();
                return Mono.just(response);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                return Mono.error(e);
            }
        });
        VisitsServiceClient client = new VisitsServiceClient(builder);
        java.util.List<VisitsServiceClient.VisitDTO> result = client.getVisitsByVetFrom(7, "2025-12-31").block();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).vetId()).isEqualTo(7);
    }
}
