package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.vets.model.specialty.Specialty;
import org.springframework.samples.petclinic.vets.model.specialty.SpecialtyMapper;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetDTO;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.vet.Vet;
import org.springframework.samples.petclinic.vets.model.vet.VetEntityMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VetEntityMapperTest {
    @Test
    void map_postDTO_setsNames() {
        VetEntityMapper mapper = new VetEntityMapper(new SpecialtyMapper());
        Vet v = new Vet();
        mapper.map(v, new VetPostDTO("John", "Doe", List.of()));
        assertThat(v.getFirstName()).isEqualTo("John");
        assertThat(v.getLastName()).isEqualTo("Doe");
    }

    @Test
    void map_toDTO_includesDeletedFlag_andSpecialties() {
        VetEntityMapper mapper = new VetEntityMapper(new SpecialtyMapper());
        Vet v = new Vet();
        v.setFirstName("A");
        v.setLastName("B");
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        v.setSpecialties(java.util.Set.of(s));
        v.delete();
        VetDTO dto = mapper.map(v);
        assertThat(dto.deleted()).isTrue();
        assertThat(dto.specialties()).hasSize(1);
    }
}
