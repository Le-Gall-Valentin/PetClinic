package org.springframework.samples.petclinic.vets.model.vet.DTO;

import org.springframework.samples.petclinic.vets.model.specialty.DTO.SpecialtyDTO;

import java.util.List;

public record VetDTO(
    Integer id,
    String firstName,
    String lastName,
    List<SpecialtyDTO> specialties,
    boolean deleted
) {
}
