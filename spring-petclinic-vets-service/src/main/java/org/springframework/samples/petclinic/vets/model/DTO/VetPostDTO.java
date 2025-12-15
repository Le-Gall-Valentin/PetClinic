package org.springframework.samples.petclinic.vets.model.DTO;

import jakarta.validation.constraints.NotBlank;

public record VetPostDTO(
    @NotBlank String firstName,
    @NotBlank String lastName
) {
}
