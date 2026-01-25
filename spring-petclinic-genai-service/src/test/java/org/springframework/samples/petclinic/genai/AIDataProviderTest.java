package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.samples.petclinic.genai.dto.Vet;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AIDataProviderTest {

    @Test
    void getVets_withNullRequest_returnsUpTo50() throws Exception {
        VectorStore store = Mockito.mock(VectorStore.class);
        when(store.similaritySearch(any(SearchRequest.class)))
            .thenReturn(List.of(new Document("A"), new Document("B")));
        AIDataProvider provider = new AIDataProvider(WebClient.builder(), store);

        VetResponse response = provider.getVets(new VetRequest(null));
        assertThat(response.vet()).contains("A", "B");
    }

    @Test
    void getVets_withVetFilters_returnsTop20() throws Exception {
        VectorStore store = Mockito.mock(VectorStore.class);
        when(store.similaritySearch(any(SearchRequest.class)))
            .thenReturn(List.of(new Document("X")));
        AIDataProvider provider = new AIDataProvider(WebClient.builder(), store);

        Vet vet = new Vet(1, "first", "last", java.util.Set.of());
        VetResponse response = provider.getVets(new VetRequest(vet));
        assertThat(response.vet()).containsExactly("X");
    }
}
