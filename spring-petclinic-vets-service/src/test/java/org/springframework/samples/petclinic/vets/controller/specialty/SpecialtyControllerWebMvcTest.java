package org.springframework.samples.petclinic.vets.controller.specialty;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.samples.petclinic.vets.model.specialty.Specialty;
import org.springframework.samples.petclinic.vets.model.specialty.SpecialtyMapper;
import org.springframework.samples.petclinic.vets.service.specialty.SpecialtyService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecialtyController.class)
@ActiveProfiles("test")
class SpecialtyControllerWebMvcTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private SpecialtyService specialtyService;
    @MockitoBean
    private SpecialtyMapper specialtyMapper;

    @Test
    void getAllSpecialties_returnsList() throws Exception {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        when(specialtyService.getAllSpecialties()).thenReturn(List.of(s));
        when(specialtyMapper.map(s)).thenReturn(new org.springframework.samples.petclinic.vets.model.specialty.DTO.SpecialtyDTO(1, "radiology"));
        mvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }
}
