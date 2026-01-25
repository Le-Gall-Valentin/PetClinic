package org.springframework.samples.petclinic.genai;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.genai.dto.OwnerDetails;
import org.springframework.samples.petclinic.genai.dto.PetDetails;
import org.springframework.samples.petclinic.genai.dto.PetType;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AIFunctionConfigurationTest {

    @Test
    void listOwners_callsProvider() {
        AIDataProvider provider = Mockito.mock(AIDataProvider.class);
        OwnerDetails od = new OwnerDetails(1, "a", "b", "addr", "city", "000", List.of());
        when(provider.getAllOwners()).thenReturn(new OwnersResponse(List.of(od)));

        Function<OwnerRequest, OwnersResponse> fn =
            new AIFunctionConfiguration().listOwners(provider);

        OwnersResponse response =
            fn.apply(new OwnerRequest("a", "b", "addr", "city", "000"));
        assertThat(response.owners()).hasSize(1);
    }

    @Test
    void listVets_returnsNullOnException() throws Exception {
        AIDataProvider provider = Mockito.mock(AIDataProvider.class);
        when(provider.getVets(any(VetRequest.class))).thenThrow(new JsonProcessingException("x"){});
        Function<VetRequest, VetResponse> fn =
            new AIFunctionConfiguration().listVets(provider);
        VetResponse response = fn.apply(new VetRequest(null));
        assertThat(response).isNull();
    }

    @Test
    void addPetToOwner_delegatesToProvider() {
        AIDataProvider provider = Mockito.mock(AIDataProvider.class);
        PetDetails pet = new PetDetails(1, "name", "2020-01-01", new PetType("cat"), List.of());
        when(provider.addPetToOwner(any(AddPetRequest.class)))
            .thenReturn(new AddedPetResponse(pet));

        Function<AddPetRequest, AddedPetResponse> fn =
            new AIFunctionConfiguration().addPetToOwner(provider);

        AddedPetResponse resp =
            fn.apply(new AddPetRequest(new org.springframework.samples.petclinic.genai.dto.PetRequest(1,new java.util.Date(), "n", 1), 1));
        assertThat(resp.pet()).isNotNull();
    }
}
