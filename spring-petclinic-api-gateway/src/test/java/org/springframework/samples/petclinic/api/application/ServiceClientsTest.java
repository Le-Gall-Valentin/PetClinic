package org.springframework.samples.petclinic.api.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.api.dto.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceClientsTest {

    private WebClient.Builder mockBuilderReturning(Object body) {
        return WebClient.builder().exchangeFunction(request -> {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String json = mapper.writeValueAsString(body);
                org.springframework.core.io.buffer.DataBuffer buffer =
                    new org.springframework.core.io.buffer.DefaultDataBufferFactory()
                        .wrap(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                org.springframework.web.reactive.function.client.ClientResponse response =
                    org.springframework.web.reactive.function.client.ClientResponse
                        .create(org.springframework.http.HttpStatus.OK, org.springframework.web.reactive.function.client.ExchangeStrategies.withDefaults())
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .body(reactor.core.publisher.Flux.just(buffer))
                        .build();
                return Mono.just(response);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                return Mono.error(e);
            }
        });
    }

    @Test
    void customersServiceClient_getOwner_returnsOwner() {
        OwnerDetails owner = new OwnerDetails(1, "A", "B", "addr", "city", "000", List.of());
        CustomersServiceClient client = new CustomersServiceClient(mockBuilderReturning(owner));
        OwnerDetails result = client.getOwner(1).block();
        Assertions.assertNotNull(result);
        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void petServiceClient_getPet_returnsPet() {
        PetDetails pet = new PetDetails(10, "Garfield", "2010-01-01", new PetType(1, "cat"), List.of(), false);
        PetServiceClient client = new PetServiceClient(mockBuilderReturning(pet));
        PetDetails result = client.getPet(10).block();
        Assertions.assertNotNull(result);
        assertThat(result.name()).isEqualTo("Garfield");
    }

    @Test
    void vetServiceClient_getVet_returnsVet() {
        VetDetails vet = new VetDetails(7, "A", "B", List.of(), false);
        VetServiceClient client = new VetServiceClient(mockBuilderReturning(vet));
        VetDetails result = client.getVet(7).block();
        Assertions.assertNotNull(result);
        assertThat(result.id()).isEqualTo(7);
    }

    @Test
    void vetServiceClient_getVetsForVisits_returnsVets() {
        Vets vets = new Vets(List.of(new VetDetails(7, "A", "B", List.of(), false)));
        VetServiceClient client = new VetServiceClient(mockBuilderReturning(vets));
        Vets result = client.getVetsForVisits(List.of(7)).block();
        Assertions.assertNotNull(result);
        assertThat(result.vets()).hasSize(1);
    }

    @Test
    void visitsServiceClient_getVisitsForPets_returnsVisits() {
        Visits visits = new Visits(List.of(new VisitDetails(100, 10, "2020-01-01", "desc", 7)));
        VisitsServiceClient client = new VisitsServiceClient(mockBuilderReturning(visits));
        Visits result = client.getVisitsForPets(List.of(10)).block();
        Assertions.assertNotNull(result);
        assertThat(result.items()).hasSize(1);
    }

    @Test
    void visitsServiceClient_getVisitsByVet_returnsList() {
        VisitDetails v = new VisitDetails(100, 10, "2020-01-01", "desc", 7);
        VisitsServiceClient client = new VisitsServiceClient(mockBuilderReturning(v));
        List<VisitDetails> result = client.getVisitsByVet(7, null, null).block();
        assertThat(result).hasSize(1);
    }
}
