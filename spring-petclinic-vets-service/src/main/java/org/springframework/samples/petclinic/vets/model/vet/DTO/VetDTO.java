package org.springframework.samples.petclinic.vets.model.DTO;

import org.springframework.samples.petclinic.vets.model.Specialty;

import java.util.List;

public record VetDTO(
    Integer id,
    String firstName,
    String lastName,
    List<Specialty> specialties
) {
}
