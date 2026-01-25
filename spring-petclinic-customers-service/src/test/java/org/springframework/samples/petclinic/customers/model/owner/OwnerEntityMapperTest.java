package org.springframework.samples.petclinic.customers.model.owner;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerCreateDTO;
import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerDTO;
import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerUpdateDTO;
import org.springframework.samples.petclinic.customers.model.pet.PetEntityMapper;
import org.springframework.samples.petclinic.customers.model.pet.Pet;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerEntityMapperTest {

    @Test
    void map_create_update_and_toDTO() {
        PetEntityMapper petMapper = new PetEntityMapper();
        OwnerEntityMapper mapper = new OwnerEntityMapper(petMapper);
        Owner owner = new Owner();
        OwnerCreateDTO create = new OwnerCreateDTO("John","Doe","addr","city","000");
        owner = mapper.map(owner, create);
        assertThat(owner.getFirstName()).isEqualTo("John");
        assertThat(owner.getCity()).isEqualTo("city");

        OwnerUpdateDTO update = new OwnerUpdateDTO("Jane","Smith","addr2","city2","111");
        owner = mapper.map(owner, update);
        assertThat(owner.getFirstName()).isEqualTo("Jane");
        assertThat(owner.getAddress()).isEqualTo("addr2");

        Pet pet = new Pet();
        pet.setId(1);
        owner.addPet(pet);
        OwnerDTO dto = mapper.map(owner);
        assertThat(dto.pets()).hasSize(1);
    }
}
