package org.springframework.samples.petclinic.api.application;

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

import org.springframework.samples.petclinic.api.dto.Vets;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * @author Maciej Szarlinski
 */
@Component
public class VetServiceClient {

    private final WebClient.Builder webClientBuilder;
    // Could be changed for testing purpose
    private String hostname = "http://vets-service/";

    public VetServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<Vets> getVetsForVisits(final List<Integer> vetIds) {
        return webClientBuilder.build()
            .get()
            .uri(hostname + "visits/vets?vetId={vetId}", joinIds(vetIds))
            .retrieve()
            .bodyToMono(Vets.class);
    }

    private String joinIds(List<Integer> petIds) {
        return petIds.stream().map(Object::toString).collect(joining(","));
    }

    void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
