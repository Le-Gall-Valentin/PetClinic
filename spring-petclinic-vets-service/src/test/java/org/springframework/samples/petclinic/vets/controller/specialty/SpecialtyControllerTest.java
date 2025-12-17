package org.springframework.samples.petclinic.vets.controller.specialty;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vets.model.specialty.DTO.SpecialtyDTO;
import org.springframework.samples.petclinic.vets.model.specialty.Specialty;
import org.springframework.samples.petclinic.vets.model.specialty.SpecialtyMapper;
import org.springframework.samples.petclinic.vets.service.specialty.SpecialtyService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecialtyController.class)
@ActiveProfiles("test")
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecialtyService specialtyService;

    @MockitoBean
    private SpecialtyMapper specialtyMapper;

    @Test
    void getAll_returnsList() throws Exception {
        Specialty s1 = new Specialty(); s1.setId(1); s1.setName("radiology");
        Specialty s2 = new Specialty(); s2.setId(2); s2.setName("surgery");
        when(specialtyService.getAllSpecialties()).thenReturn(List.of(s1, s2));
        when(specialtyMapper.map(s1)).thenReturn(new SpecialtyDTO(1, "radiology"));
        when(specialtyMapper.map(s2)).thenReturn(new SpecialtyDTO(2, "surgery"));

        mockMvc.perform(get("/specialties").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[1].name", is("surgery")));
    }
}
