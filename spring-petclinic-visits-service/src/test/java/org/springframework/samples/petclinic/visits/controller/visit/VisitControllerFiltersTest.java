package org.springframework.samples.petclinic.visits.controller.visit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.visit.VisitEntityMapper;
import org.springframework.samples.petclinic.visits.service.visit.VisitService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitController.class)
@Import(VisitEntityMapper.class)
@ActiveProfiles("test")
class VisitControllerFiltersTest {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private VisitService visitService;

    @Test
    void readByVet_fromOnly() throws Exception {
        when(visitService.getVisitsByVetIdWithDateFilter(org.mockito.ArgumentMatchers.eq(2), any(), org.mockito.ArgumentMatchers.isNull()))
            .thenReturn(List.of(Visit.VisitBuilder.aVisit().id(8).petId(7).vetId(2).build()));
        mvc.perform(get("/vets/2/visits?from=2024-01-01"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(8));
    }

    @Test
    void readByVet_toOnly() throws Exception {
        when(visitService.getVisitsByVetIdWithDateFilter(org.mockito.ArgumentMatchers.eq(2), org.mockito.ArgumentMatchers.isNull(), any()))
            .thenReturn(List.of(Visit.VisitBuilder.aVisit().id(9).petId(7).vetId(2).build()));
        mvc.perform(get("/vets/2/visits?to=2025-01-01"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(9));
    }
}
