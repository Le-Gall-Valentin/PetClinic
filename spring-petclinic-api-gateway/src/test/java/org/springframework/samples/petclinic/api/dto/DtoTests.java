package org.springframework.samples.petclinic.api.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTests {

    @Test
    void vetPage_initializesVisitsWhenNull() {
        VetDetails vet = new VetDetails(1, "A", "B", List.of(), false);
        VetPage page = new VetPage(vet, null);
        assertThat(page.visits()).isNotNull();
        assertThat(page.visits()).isEmpty();
    }

    @Test
    void petDetails_builder_builds() {
        PetType type = new PetType(1, "cat");
        PetDetails pet = PetDetails.PetDetailsBuilder.aPetDetails()
            .id(10)
            .name("Garfield")
            .birthDate("2010-01-01")
            .type(type)
            .visits(List.of(new VisitDetails(100, 10, "2020-01-01", "desc", 7)))
            .deleted(false)
            .build();
        assertThat(pet.id()).isEqualTo(10);
        assertThat(pet.type().name()).isEqualTo("cat");
        assertThat(pet.visits()).hasSize(1);
    }
}
