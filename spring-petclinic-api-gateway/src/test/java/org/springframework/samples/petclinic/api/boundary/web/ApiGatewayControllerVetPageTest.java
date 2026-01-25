package org.springframework.samples.petclinic.api.boundary.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.api.application.CustomersServiceClient;
import org.springframework.samples.petclinic.api.application.PetServiceClient;
import org.springframework.samples.petclinic.api.application.VetServiceClient;
import org.springframework.samples.petclinic.api.application.VisitsServiceClient;
import org.springframework.samples.petclinic.api.dto.PetDetails;
import org.springframework.samples.petclinic.api.dto.PetType;
import org.springframework.samples.petclinic.api.dto.VetDetails;
import org.springframework.samples.petclinic.api.dto.VisitDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ApiGatewayController.class)
@Import({ReactiveResilience4JAutoConfiguration.class, CircuitBreakerConfiguration.class})
class ApiGatewayControllerVetPageTest {

    @MockBean
    private CustomersServiceClient customersServiceClient;
    @MockBean
    private VisitsServiceClient visitsServiceClient;
    @MockBean
    private VetServiceClient vetServiceClient;
    @MockBean
    private PetServiceClient petServiceClient;

    @Autowired
    private WebTestClient client;

    @Test
    void getVetPage_returnsEmptyVisits_whenNoPetIds() {
        when(vetServiceClient.getVet(7)).thenReturn(Mono.just(new VetDetails(7, "A", "B", java.util.List.of(), false)));
        when(visitsServiceClient.getVisitsByVet(7, null, null)).thenReturn(Mono.just(java.util.List.of()));

        client.get()
            .uri("/api/gateway/vets/7")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.vet.id").isEqualTo(7)
            .jsonPath("$.visits").isEmpty();
    }

    @Test
    void getVetPage_enrichesVisitsWithPets() {
        when(vetServiceClient.getVet(7)).thenReturn(Mono.just(new VetDetails(7, "A", "B", java.util.List.of(), false)));
        VisitDetails v = new VisitDetails(100, 10, "2020-01-01", "desc", 7);
        when(visitsServiceClient.getVisitsByVet(7, null, null)).thenReturn(Mono.just(java.util.List.of(v)));
        PetDetails pet = new PetDetails(10, "Garfield", "2010-01-01", new PetType(1, "cat"), java.util.List.of(), false);
        when(petServiceClient.getPet(10)).thenReturn(Mono.just(pet));

        client.get()
            .uri("/api/gateway/vets/7")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.visits[0].visit.id").isEqualTo(100)
            .jsonPath("$.visits[0].pet.name").isEqualTo("Garfield");
    }

    @Test
    void getVetPage_withFromOnly() {
        when(vetServiceClient.getVet(7)).thenReturn(Mono.just(new VetDetails(7, "A", "B", java.util.List.of(), false)));
        VisitDetails v = new VisitDetails(101, 11, "2020-01-02", "desc2", 7);
        when(visitsServiceClient.getVisitsByVet(7, "2020-01-01", null)).thenReturn(Mono.just(java.util.List.of(v)));
        PetDetails pet = new PetDetails(11, "Nermal", "2011-01-01", new PetType(2, "cat"), java.util.List.of(), false);
        when(petServiceClient.getPet(11)).thenReturn(Mono.just(pet));

        client.get()
            .uri("/api/gateway/vets/7?from=2020-01-01")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.visits[0].visit.id").isEqualTo(101)
            .jsonPath("$.visits[0].pet.name").isEqualTo("Nermal");
    }

    @Test
    void getVetPage_withToOnly() {
        when(vetServiceClient.getVet(7)).thenReturn(Mono.just(new VetDetails(7, "A", "B", java.util.List.of(), false)));
        VisitDetails v = new VisitDetails(102, 12, "2020-01-03", "desc3", 7);
        when(visitsServiceClient.getVisitsByVet(7, null, "2020-02-01")).thenReturn(Mono.just(java.util.List.of(v)));
        PetDetails pet = new PetDetails(12, "Odie", "2012-01-01", new PetType(3, "dog"), java.util.List.of(), false);
        when(petServiceClient.getPet(12)).thenReturn(Mono.just(pet));

        client.get()
            .uri("/api/gateway/vets/7?to=2020-02-01")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.visits[0].visit.id").isEqualTo(102)
            .jsonPath("$.visits[0].pet.name").isEqualTo("Odie");
    }

    @Test
    void getVetPage_fallbacksToEmptyOnPetServiceError() {
        when(vetServiceClient.getVet(7)).thenReturn(Mono.just(new VetDetails(7, "A", "B", java.util.List.of(), false)));
        VisitDetails v = new VisitDetails(100, 10, "2020-01-01", "desc", 7);
        when(visitsServiceClient.getVisitsByVet(7, null, null)).thenReturn(Mono.just(java.util.List.of(v)));
        when(petServiceClient.getPet(anyInt())).thenReturn(Mono.error(new RuntimeException("boom")));

        client.get()
            .uri("/api/gateway/vets/7")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.visits").isEmpty();
    }
}
