package org.springframework.samples.petclinic.visits.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.visits.controller.visit.VisitController;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.visit.VisitEntityMapper;
import org.springframework.samples.petclinic.visits.service.visit.VisitService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VisitController.class)
@Import(VisitEntityMapper.class)
@ActiveProfiles("test")
class VisitResourceTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    VisitService visitService;

    @Test
    void shouldFetchVisits() throws Exception {
        given(visitService.getVisitsByPetIds(asList(111, 222)))
            .willReturn(
                asList(
                    Visit.VisitBuilder.aVisit()
                        .id(1)
                        .petId(111)
                        .build(),
                    Visit.VisitBuilder.aVisit()
                        .id(2)
                        .petId(222)
                        .build(),
                    Visit.VisitBuilder.aVisit()
                        .id(3)
                        .petId(222)
                        .build()
                )
            );

        mvc.perform(get("/pets/visits?petId=111,222"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].id").value(1))
            .andExpect(jsonPath("$.items[1].id").value(2))
            .andExpect(jsonPath("$.items[2].id").value(3))
            .andExpect(jsonPath("$.items[0].petId").value(111))
            .andExpect(jsonPath("$.items[1].petId").value(222))
            .andExpect(jsonPath("$.items[2].petId").value(222));
    }

    @Test
    void shouldCreateVisit() throws Exception {
        given(visitService.createVisit(123, new org.springframework.samples.petclinic.visits.model.visit.DTO.VisitPostDTO(null, "Checkup", null)))
            .willReturn(
                Visit.VisitBuilder.aVisit()
                    .id(10)
                    .petId(123)
                    .description("Checkup")
                    .vetId(null)
                    .build()
            );

        mvc.perform(post("/owners/*/pets/123/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Checkup\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.petId").value(123))
            .andExpect(jsonPath("$.description").value("Checkup"));
    }

    @Test
    void shouldCreateVisitWithVet() throws Exception {
        given(visitService.createVisit(123, new org.springframework.samples.petclinic.visits.model.visit.DTO.VisitPostDTO(null, "Surgery", 2)))
            .willReturn(
                Visit.VisitBuilder.aVisit()
                    .id(11)
                    .petId(123)
                    .description("Surgery")
                    .vetId(2)
                    .build()
            );

        mvc.perform(post("/owners/*/pets/123/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Surgery\",\"vetId\":2}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(11))
            .andExpect(jsonPath("$.petId").value(123))
            .andExpect(jsonPath("$.description").value("Surgery"))
            .andExpect(jsonPath("$.vetId").value(2));
    }

    @Test
    void shouldReadByPetId() throws Exception {
        given(visitService.getVisitsByPetId(111))
            .willReturn(
                asList(
                    Visit.VisitBuilder.aVisit().id(1).petId(111).vetId(2).build(),
                    Visit.VisitBuilder.aVisit().id(2).petId(111).vetId(null).build()
                )
            );

        mvc.perform(get("/owners/*/pets/111/visits"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void shouldReadByVetId() throws Exception {
        given(visitService.getVisitsByVetIdWithDateFilter(eq(2), eq(null), eq(null)))
            .willReturn(
                asList(
                    Visit.VisitBuilder.aVisit().id(5).petId(7).vetId(2).build(),
                    Visit.VisitBuilder.aVisit().id(6).petId(8).vetId(2).build()
                )
            );

        mvc.perform(get("/vets/2/visits"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].vetId").value(2))
            .andExpect(jsonPath("$[1].vetId").value(2));
    }

    @Test
    void shouldFilterByDateRange() throws Exception {
        given(visitService.getVisitsByVetIdWithDateFilter(eq(2), any(), any()))
            .willReturn(
                asList(
                    Visit.VisitBuilder.aVisit().id(7).petId(7).vetId(2).build()
                )
            );
        mvc.perform(get("/vets/2/visits?from=2024-01-01&to=2025-01-01"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(7));
        verify(visitService).getVisitsByVetIdWithDateFilter(eq(2), any(), any());
    }

    @Test
    void shouldDeleteFutureVisit() throws Exception {
        doNothing().when(visitService).deleteVisit(100);
        mvc.perform(delete("/visits/100"))
            .andExpect(status().isNoContent());
        verify(visitService).deleteVisit(100);
    }

    @Test
    void shouldRejectDeletePastVisit() throws Exception {
        doThrow(new IllegalStateException("Only future visits can be deleted")).when(visitService).deleteVisit(1);
        mvc.perform(delete("/visits/1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundOnDeleteUnknownVisit() throws Exception {
        doThrow(new jakarta.persistence.EntityNotFoundException("Visit not found")).when(visitService).deleteVisit(999);
        mvc.perform(delete("/visits/999"))
            .andExpect(status().isNotFound());
    }
    @Test
    void shouldRejectInvalidPetId() throws Exception {
        mvc.perform(get("/owners/*/pets/0/visits"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectTooLongDescription() throws Exception {
        final String longDesc = "a".repeat(9000);
        mvc.perform(post("/owners/*/pets/111/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"" + longDesc + "\"}"))
            .andExpect(status().isBadRequest());
    }
}
