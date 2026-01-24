package org.springframework.samples.petclinic.vets.service.visit;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class VisitsServiceClient {
    private final WebClient.Builder webClientBuilder;
    private String hostname = "http://visits-service/";

    public VisitsServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<List<VisitDTO>> getVisitsByVetFrom(final int vetId, final String fromIsoDate) {
        return webClientBuilder.build()
            .get()
            .uri(hostname + "vets/{vetId}/visits?from={from}", vetId, fromIsoDate)
            .retrieve()
            .bodyToFlux(VisitDTO.class)
            .collectList();
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public record VisitDTO(Integer id, Integer petId, Integer vetId, String date, String description) {}
}
