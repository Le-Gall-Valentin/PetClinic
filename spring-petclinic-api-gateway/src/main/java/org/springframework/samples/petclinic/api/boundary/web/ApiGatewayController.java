/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.api.boundary.web;

import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.samples.petclinic.api.application.CustomersServiceClient;
import org.springframework.samples.petclinic.api.application.PetServiceClient;
import org.springframework.samples.petclinic.api.application.VetServiceClient;
import org.springframework.samples.petclinic.api.application.VisitsServiceClient;
import org.springframework.samples.petclinic.api.dto.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Maciej Szarlinski
 */
@RestController
@RequestMapping("/api/gateway")
public class ApiGatewayController {

    private final CustomersServiceClient customersServiceClient;
    private final VetServiceClient vetServiceClient;
    private final PetServiceClient petServiceClient;

    private final VisitsServiceClient visitsServiceClient;

    private final ReactiveCircuitBreakerFactory cbFactory;

    public ApiGatewayController(CustomersServiceClient customersServiceClient,
                                VetServiceClient vetServiceClient,
                                PetServiceClient petServiceClient,
                                VisitsServiceClient visitsServiceClient,
                                ReactiveCircuitBreakerFactory cbFactory) {
        this.customersServiceClient = customersServiceClient;
        this.vetServiceClient = vetServiceClient;
        this.petServiceClient = petServiceClient;
        this.visitsServiceClient = visitsServiceClient;
        this.cbFactory = cbFactory;
    }

    @GetMapping(value = "owners/{ownerId}")
    public Mono<OwnerDetails> getOwnerDetails(final @PathVariable int ownerId) {
        return customersServiceClient.getOwner(ownerId)
            .flatMap(owner ->
                visitsServiceClient.getVisitsForPets(owner.getPetIds())
                    .transform(it -> {
                        ReactiveCircuitBreaker cb = cbFactory.create("getOwnerDetails");
                        return cb.run(it, throwable -> emptyVisitsForPets());
                    })
                    .map(addVisitsToOwner(owner))
            );

    }

    private Function<Visits, OwnerDetails> addVisitsToOwner(OwnerDetails owner) {
        return visits -> {
            owner.pets()
                .forEach(pet -> pet.visits()
                    .addAll(visits.items().stream()
                        .filter(v -> v.petId() == pet.id())
                        .toList())
                );
            return owner;
        };
    }

    private Mono<Visits> emptyVisitsForPets() {
        return Mono.just(new Visits(List.of()));
    }

    @GetMapping("vets/{vetId}")
    public Mono<VetPage> getVetPage(final @PathVariable int vetId,
                                    @RequestParam(value = "from", required = false) String from,
                                    @RequestParam(value = "to", required = false) String to) {
        final Mono<VetDetails> vetMono = vetServiceClient.getVet(vetId);
        final Mono<List<VisitDetails>> visitsMono = visitsServiceClient.getVisitsByVet(vetId, from, to);

        return Mono.zip(vetMono, visitsMono)
            .flatMap(tuple -> {
                final VetDetails vet = tuple.getT1();
                final List<VisitDetails> visits = tuple.getT2();
                final List<Integer> petIds = visits.stream().map(VisitDetails::petId).distinct().toList();
                if (petIds.isEmpty()) {
                    return Mono.just(new VetPage(vet, List.of()));
                }
                final ReactiveCircuitBreaker cb = cbFactory.create("getVetPage");
                final List<Mono<org.springframework.samples.petclinic.api.dto.PetDetails>> petCalls =
                    petIds.stream().map(petServiceClient::getPet).toList();
                return cb.run(Mono.zip(petCalls, objects -> {
                        final Map<Integer, org.springframework.samples.petclinic.api.dto.PetDetails> map = Arrays.stream(objects)
                            .map(PetDetails.class::cast)
                            .collect(Collectors.toMap(org.springframework.samples.petclinic.api.dto.PetDetails::id, Function.identity()));
                        final List<VisitWithPet> enriched = visits.stream()
                            .map(v -> new VisitWithPet(v, map.get(v.petId())))
                            .toList();
                        return new VetPage(vet, enriched);
                    }),
                    throwable -> Mono.just(new VetPage(vet, List.of()))
                );
            });
    }
}
