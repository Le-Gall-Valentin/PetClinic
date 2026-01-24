package org.springframework.samples.petclinic.customers.controller.pet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.owner.Owner;
import org.springframework.samples.petclinic.customers.model.pet.Pet;
import org.springframework.samples.petclinic.customers.model.pet.PetEntityMapper;
import org.springframework.samples.petclinic.customers.model.pet.dto.PetDTO;
import org.springframework.samples.petclinic.customers.model.pettype.PetType;
import org.springframework.samples.petclinic.customers.model.pettype.PetTypeEntityMapper;
import org.springframework.samples.petclinic.customers.service.pet.PetService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Maciej Szarlinski
 */
@WebMvcTest(PetController.class)
@ActiveProfiles("test")
class PetControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PetService petService;
    @MockitoBean
    private PetEntityMapper petEntityMapper;
    @MockitoBean
    private PetTypeEntityMapper petTypeEntityMapper;

    @Test
    void shouldGetAPetInJSonFormat() throws Exception {

        Pet pet = setupPet();

        when(petService.findPetById(2)).thenReturn(pet);
        when(petEntityMapper.map(pet)).thenReturn(new PetDTO(pet));

        mvc.perform(get("/owners/2/pets/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Basil"))
            .andExpect(jsonPath("$.type.id").value(6));
    }

    private Pet setupPet() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        Pet pet = new Pet();

        pet.setName("Basil");
        pet.setId(2);

        PetType petType = new PetType();
        petType.setId(6);
        pet.setType(petType);

        owner.addPet(pet);
        return pet;
    }
}
