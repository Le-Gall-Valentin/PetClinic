package org.springframework.samples.petclinic.api.dto;

import java.util.ArrayList;
import java.util.List;

public record Vets(
    List<VetDetails> vets
) {
    public Vets() {
        this(new ArrayList<>());
    }
}
