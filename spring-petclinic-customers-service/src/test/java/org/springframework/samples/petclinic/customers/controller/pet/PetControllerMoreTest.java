package org.springframework.samples.petclinic.customers.controller.pet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.pet.Pet;
import org.springframework.samples.petclinic.customers.model.pet.PetEntityMapper;
import org.springframework.samples.petclinic.customers.model.pet.dto.PetDTO;
import org.springframework.samples.petclinic.customers.model.pettype.PetTypeEntityMapper;
import org.springframework.samples.petclinic.customers.service.pet.PetService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
@ActiveProfiles("test")
class PetControllerMoreTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PetService petService;
    @MockitoBean
    private PetEntityMapper petEntityMapper;
    @MockitoBean
    private PetTypeEntityMapper petTypeEntityMapper;

    @Test
    void processCreationForm_returns201() throws Exception {
        Pet pet = new Pet();
        pet.setId(42);
        org.springframework.samples.petclinic.customers.model.owner.Owner owner = new org.springframework.samples.petclinic.customers.model.owner.Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        org.springframework.samples.petclinic.customers.model.pettype.PetType type = new org.springframework.samples.petclinic.customers.model.pettype.PetType();
        type.setId(6);
        type.setName("cat");
        pet.setOwner(owner);
        pet.setType(type);
        when(petEntityMapper.map(any(Pet.class), any(org.springframework.samples.petclinic.customers.model.pet.dto.PetPostDTO.class))).thenReturn(new Pet());
        when(petService.createPet(any(Pet.class), any(Integer.class), any(Integer.class))).thenReturn(pet);
        when(petEntityMapper.map(pet)).thenReturn(new PetDTO(pet));

        String body = """
            {"birthDate":"2020-01-01","name":"Basil","typeId":6}
        """;
        mvc.perform(post("/owners/2/pets").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location","/owners/*/pets/42"))
            .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void processUpdateForm_returns200() throws Exception {
        Pet updated = new Pet();
        updated.setId(43);
        org.springframework.samples.petclinic.customers.model.owner.Owner owner2 = new org.springframework.samples.petclinic.customers.model.owner.Owner();
        owner2.setFirstName("George");
        owner2.setLastName("Bush");
        org.springframework.samples.petclinic.customers.model.pettype.PetType type2 = new org.springframework.samples.petclinic.customers.model.pettype.PetType();
        type2.setId(6);
        type2.setName("cat");
        updated.setOwner(owner2);
        updated.setType(type2);
        when(petEntityMapper.map(any(Pet.class), any(org.springframework.samples.petclinic.customers.model.pet.dto.PetUpdateDTO.class))).thenReturn(new Pet());
        when(petService.updatePet(any(Integer.class), any(Pet.class), any(Integer.class))).thenReturn(updated);
        when(petEntityMapper.map(updated)).thenReturn(new PetDTO(updated));

        String body = """
            {"birthDate":"2020-02-02","name":"Basil2","typeId":6}
        """;
        mvc.perform(put("/owners/*/pets/43").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(43));
    }

    @Test
    void deletePet_returns204() throws Exception {
        doNothing().when(petService).deletePet(50);
        mvc.perform(delete("/owners/*/pets/50"))
            .andExpect(status().isNoContent());
    }
}
