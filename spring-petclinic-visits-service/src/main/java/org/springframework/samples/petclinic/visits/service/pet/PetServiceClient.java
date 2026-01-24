package org.springframework.samples.petclinic.visits.service.pet;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PetServiceClient {

    private final WebClient.Builder webClientBuilder;

    public PetServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<PetDetails> getPet(final int petId) {
        return webClientBuilder.build()
            .get()
            .uri("http://customers-service/owners/*/pets/{petId}", petId)
            .retrieve()
            .bodyToMono(PetDetails.class);
    }
}
