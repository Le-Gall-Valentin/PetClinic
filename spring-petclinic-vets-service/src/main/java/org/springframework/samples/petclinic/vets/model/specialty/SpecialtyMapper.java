package org.springframework.samples.petclinic.vets.model.specialty;

import org.springframework.samples.petclinic.vets.model.specialty.DTO.SpecialtyDTO;
import org.springframework.stereotype.Component;

@Component
public class SpecialtyMapper {
    public SpecialtyDTO map(final Specialty specialty) {
        return new SpecialtyDTO(specialty.getId(), specialty.getName());
    }
}
