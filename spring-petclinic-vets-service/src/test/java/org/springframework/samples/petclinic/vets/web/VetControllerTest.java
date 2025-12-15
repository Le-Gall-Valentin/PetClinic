/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vets.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vets.controller.VetController;
import org.springframework.samples.petclinic.vets.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.vets.model.DTO.VetDTO;
import org.springframework.samples.petclinic.vets.model.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.service.VetService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Maciej Szarlinski
 */
@WebMvcTest(VetController.class)
@ActiveProfiles("test")
@Import(VetControllerTest.MockConfig.class)
class VetControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    VetService vetService;

    @Test
    void shouldGetAListOfVets() throws Exception {
        VetDTO vet = new VetDTO(1, "Jean", "Marc");
        given(vetService.getAllVets()).willReturn(List.of(vet));

        mvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));

        verify(vetService).getAllVets();
    }

    @Test
    void shouldRejectGetVetWhenIdIsInvalid() throws Exception {
        mvc.perform(get("/vets/0").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAVetWithId() throws Exception {
        VetDTO vet = new VetDTO(1, "Jean", "Marc");
        given(vetService.getVetById(1)).willReturn(vet);

        mvc.perform(get("/vets/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(vetService).getVetById(1);
    }

    @Test
    void shouldPostAVet() throws Exception {
        VetDTO vet = new VetDTO(1, "Jean", "Marc");
        VetPostDTO body = new VetPostDTO("Jean", "Marc");
        given(vetService.addVet(any(VetPostDTO.class))).willReturn(vet);

        mvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated());

        verify(vetService).addVet(any(VetPostDTO.class));
    }

    @Test
    void shouldUpdateAVet() throws Exception {
        VetPostDTO vetPostDTO = new VetPostDTO("Jean", "Marc");

        willDoNothing().given(vetService).updateVet(eq(1), any(VetPostDTO.class));

        mvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vetPostDTO)))
            .andExpect(status().isOk())
            .andExpect(content().string("")); // pas de body

        verify(vetService).updateVet(eq(1), any(VetPostDTO.class));
    }

    @Test
    void shouldRejectUpdateWhenIdIsInvalid() throws Exception {
        VetPostDTO vetPostDTO = new VetPostDTO("Jean", "Marc");

        mvc.perform(put("/vets/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vetPostDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUpdateWhenBodyIsInvalid() throws Exception {
        VetPostDTO vetPostDTO = new VetPostDTO("", ""); // si @NotBlank existe

        mvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vetPostDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectPostWhenJsonIsInvalid() throws Exception {
        mvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenVetNotFound() throws Exception {
        willThrow(new ResourceNotFoundException("Vet 999 not found"))
            .given(vetService).getVetById(999);

        mvc.perform(get("/vets/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        VetService vetService() {
            return Mockito.mock(VetService.class);
        }
    }

}
