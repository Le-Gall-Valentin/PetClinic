package org.springframework.samples.petclinic.vets.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VetApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullLifecycle_create_read_update() throws Exception {
        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", greaterThan(0)));

        String createBody = """
            {"firstName":"Ivy","lastName":"Lee","specialties":[1,2]}
            """;
        MvcResult createResult = mockMvc.perform(post("/vets").contentType(MediaType.APPLICATION_JSON).content(createBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName", is("Ivy")))
            .andReturn();
        int newId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/vets/" + newId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(newId)))
            .andExpect(jsonPath("$.specialties.length()", greaterThan(0)));

        String updateBody = """
            {"firstName":"Ivy2","lastName":"Lee2","specialties":[1]}
            """;
        mockMvc.perform(put("/vets/" + newId).contentType(MediaType.APPLICATION_JSON).content(updateBody))
            .andExpect(status().isOk());

        mockMvc.perform(get("/vets/" + newId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Ivy2")))
            .andExpect(jsonPath("$.lastName", is("Lee2")));
    }
}
