package org.springframework.samples.petclinic.vets.model.vet.DTO;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record VetPostDTO(
    @NotBlank String firstName,
    @NotBlank String lastName,
    List<Integer> specialties
) {
}
