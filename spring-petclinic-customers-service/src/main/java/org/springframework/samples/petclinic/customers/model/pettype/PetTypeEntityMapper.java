package org.springframework.samples.petclinic.customers.model.pettype;

import org.springframework.samples.petclinic.customers.model.pettype.dto.PetTypeDTO;
import org.springframework.stereotype.Component;

@Component
public class PetTypeEntityMapper {

    public PetType map(final PetType petType, final PetTypeDTO petTypeDTO) {
        petType.setId(petTypeDTO.id());
        petType.setName(petTypeDTO.name());
        return petType;
    }

    public PetTypeDTO map(final PetType petType) {
        return new PetTypeDTO(petType.getId(), petType.getName());
    }
}
