package org.springframework.samples.petclinic.genai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VectorStoreControllerTest {

    @Test
    void convertListToJsonResource_returnsResource() {
        VectorStoreController controller = new VectorStoreController(
            Mockito.mock(VectorStore.class),
            WebClient.builder(),
            new ObjectMapper()
        );
        Resource res = controller.convertListToJsonResource(List.of(new org.springframework.samples.petclinic.genai.dto.Vet(1, "a", "b", java.util.Set.of())));
        assertThat(res).isNotNull();
    }

    @Test
    void convertListToJsonResource_returnsNullOnException() {
        ObjectMapper throwing = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
                throw new JsonProcessingException("boom"){ };
            }
        };
        VectorStoreController controller = new VectorStoreController(
            Mockito.mock(VectorStore.class),
            WebClient.builder(),
            throwing
        );
        Resource res = controller.convertListToJsonResource(List.of(new org.springframework.samples.petclinic.genai.dto.Vet(1, "a", "b", java.util.Set.of())));
        assertThat(res).isNull();
    }
}
