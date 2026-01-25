package org.springframework.samples.petclinic.vets.controller.vet;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vets.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetDTO;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.vet.Vet;
import org.springframework.samples.petclinic.vets.model.vet.VetEntityMapper;
import org.springframework.samples.petclinic.vets.service.vet.VetService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetController.class)
@ActiveProfiles("test")
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetService vetService;

    @MockitoBean
    private VetEntityMapper vetEntityMapper;

    @Test
    void getVets_returnsList() throws Exception {
        Vet v1 = new Vet();
        v1.setId(1);
        v1.setFirstName("A");
        v1.setLastName("B");
        Vet v2 = new Vet();
        v2.setId(2);
        v2.setFirstName("C");
        v2.setLastName("D");
        when(vetService.getAllVets()).thenReturn(List.of(v1, v2));
        when(vetEntityMapper.map(v1)).thenReturn(new VetDTO(1, "A", "B", List.of(), false));
        when(vetEntityMapper.map(v2)).thenReturn(new VetDTO(2, "C", "D", List.of(), false));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void postVet_valid_returns201() throws Exception {
        Vet created = new Vet();
        created.setId(10);
        created.setFirstName("John");
        created.setLastName("Doe");
        when(vetService.addVet(any(VetPostDTO.class))).thenReturn(created);
        when(vetEntityMapper.map(created)).thenReturn(new VetDTO(10, "John", "Doe", List.of(), false));

        String body = """
            {"firstName":"John","lastName":"Doe","specialties":[1,2]}
            """;
        mockMvc.perform(post("/vets").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(10)))
            .andExpect(jsonPath("$.firstName", is("John")))
            .andExpect(jsonPath("$.lastName", is("Doe")));
    }

    @Test
    void postVet_invalid_returns400() throws Exception {
        String body = """
            {"firstName":"","lastName":"","specialties":[1]}
            """;
        mockMvc.perform(post("/vets").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void putVet_ok_returns200() throws Exception {
        String body = """
            {"firstName":"X","lastName":"Y","specialties":[]}
            """;
        mockMvc.perform(put("/vets/1").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk());
        Mockito.verify(vetService).updateVet(eq(1), any(VetPostDTO.class));
    }

    @Test
    void putVet_notFound_returns404() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("not found")).when(vetService).updateVet(eq(99), any(VetPostDTO.class));
        String body = """
            {"firstName":"X","lastName":"Y","specialties":[]}
            """;
        mockMvc.perform(put("/vets/99").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isNotFound());
    }

    @Test
    void getById_returns200_or404() throws Exception {
        Vet v = new Vet();
        v.setId(3);
        v.setFirstName("E");
        v.setLastName("F");
        when(vetService.getVetById(3)).thenReturn(v);
        when(vetEntityMapper.map(v)).thenReturn(new VetDTO(3, "E", "F", List.of(), false));
        mockMvc.perform(get("/vets/3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(3)));

        when(vetService.getVetById(77)).thenThrow(new ResourceNotFoundException("77"));
        mockMvc.perform(get("/vets/77"))
            .andExpect(status().isNotFound());
    }
}
