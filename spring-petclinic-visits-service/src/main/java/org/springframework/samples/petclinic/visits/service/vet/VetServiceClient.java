package org.springframework.samples.petclinic.visits.service.vet;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class VetServiceClient {
    private final WebClient.Builder webClientBuilder;
    private String hostname = "http://vets-service/";

    public VetServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<VetDetails> getVet(final int vetId) {
        return webClientBuilder.build()
            .get()
            .uri(hostname + "vets/{vetId}", vetId)
            .retrieve()
            .bodyToMono(VetDetails.class);
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public record VetDetails(Integer id, String firstName, String lastName, java.util.List<SpecialtyDetails> specialties, boolean deleted) {}
    public record SpecialtyDetails(Integer id, String name) {}
}
