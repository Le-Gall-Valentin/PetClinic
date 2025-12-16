package org.springframework.samples.petclinic.vets.model.vet.DTO;

import org.springframework.samples.petclinic.vets.model.specialty.Specialty;

import java.util.List;

public record VetDTO(
    Integer id,
    String firstName,
    String lastName,
    List<Specialty> specialties
) {
}
