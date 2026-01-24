package org.springframework.samples.petclinic.api.dto;

public record VisitWithPet(
    VisitDetails visit,
    PetDetails pet
) {}
