package org.springframework.samples.petclinic.api.dto;

import java.util.ArrayList;
import java.util.List;

public record VetPage(
    VetDetails vet,
    List<VisitWithPet> visits
) {
    public VetPage {
        if (visits == null) {
            visits = new ArrayList<>();
        }
    }
}
