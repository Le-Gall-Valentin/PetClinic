package org.springframework.samples.petclinic.vets.controller.vet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetDTO;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.vet.Vet;
import org.springframework.samples.petclinic.vets.model.vet.VetEntityMapper;
import org.springframework.samples.petclinic.vets.service.vet.VetService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VetController.class)
@ActiveProfiles("test")
class VetControllerWebMvcTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private VetService vetService;
    @MockitoBean
    private VetEntityMapper vetEntityMapper;

    @Test
    void getAll_returnsList() throws Exception {
        Vet vet = new Vet();
        vet.setFirstName("A");
        vet.setLastName("B");
        when(vetService.getAllVets()).thenReturn(List.of(vet));
        when(vetEntityMapper.map(vet)).thenReturn(new VetDTO(1,"A","B", List.of(), false));
        mvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("A"));
    }

    @Test
    void create_returns201() throws Exception {
        Vet vet = new Vet();
        vet.setFirstName("C");
        vet.setLastName("D");
        when(vetService.addVet(any(VetPostDTO.class))).thenReturn(vet);
        when(vetEntityMapper.map(vet)).thenReturn(new VetDTO(2,"C","D", List.of(), false));
        String body = """
            {"firstName":"C","lastName":"D","specialties":[1,2]}
        """;
        mvc.perform(post("/vets").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("C"));
    }

    @Test
    void update_returns200() throws Exception {
        doNothing().when(vetService).updateVet(3, new VetPostDTO("E","F", List.of(1)));
        String body = """
            {"firstName":"E","lastName":"F","specialties":[1]}
        """;
        mvc.perform(put("/vets/3").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk());
    }

    @Test
    void find_returns200() throws Exception {
        Vet vet = new Vet();
        vet.setFirstName("G");
        vet.setLastName("H");
        when(vetService.getVetById(4)).thenReturn(vet);
        when(vetEntityMapper.map(vet)).thenReturn(new VetDTO(4,"G","H", List.of(), false));
        mvc.perform(get("/vets/4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lastName").value("H"));
    }

    @Test
    void delete_returns204() throws Exception {
        doNothing().when(vetService).deleteVet(5);
        mvc.perform(delete("/vets/5"))
            .andExpect(status().isNoContent());
    }
}
