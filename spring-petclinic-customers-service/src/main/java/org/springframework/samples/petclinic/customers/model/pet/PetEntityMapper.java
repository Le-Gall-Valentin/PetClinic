package org.springframework.samples.petclinic.customers.model.pet;

import org.springframework.samples.petclinic.customers.model.pet.dto.PetDetails;
import org.springframework.samples.petclinic.customers.model.pet.dto.PetPostDTO;
import org.springframework.samples.petclinic.customers.model.pet.dto.PetUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class PetEntityMapper {

    public Pet map(final Pet pet, final PetPostDTO petRequest) {
        pet.setName(petRequest.name());
        pet.setBirthDate(petRequest.birthDate());
        return pet;
    }

    public Pet map(final Pet pet, final PetUpdateDTO petUpdateDTO) {
        pet.setName(petUpdateDTO.name());
        pet.setBirthDate(petUpdateDTO.birthDate());
        return pet;
    }

    public PetDetails map(final Pet pet) {
        return new PetDetails(pet);
    }
}
