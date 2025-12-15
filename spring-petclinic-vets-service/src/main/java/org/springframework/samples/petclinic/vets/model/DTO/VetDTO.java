package org.springframework.samples.petclinic.vets.model.DTO;

public record VetDTO(
    Integer id,
    String firstName,
    String lastName
) {
}
