package org.springframework.samples.petclinic.vets.model.vet;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.vets.model.specialty.Specialty;
import org.springframework.samples.petclinic.vets.model.specialty.SpecialtyMapper;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetDTO;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VetEntityMapperTest {

    private final SpecialtyMapper specialtyMapper = new SpecialtyMapper();
    private final VetEntityMapper mapper = new VetEntityMapper(specialtyMapper);

    @Test
    void mapFromPostDto_setsNames() {
        Vet target = new Vet();
        VetPostDTO dto = new VetPostDTO("John", "Doe", List.of());

        mapper.map(target, dto);

        assertEquals("John", target.getFirstName());
        assertEquals("Doe", target.getLastName());
    }

    @Test
    void mapToDto_mapsSpecialtiesToDto() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("Alice");
        vet.setLastName("Smith");
        Specialty s1 = new Specialty();
        s1.setId(10);
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setId(11);
        s2.setName("surgery");
        vet.addSpecialty(s1);
        vet.addSpecialty(s2);

        VetDTO dto = mapper.map(vet);

        assertNotNull(dto);
        assertEquals(1, dto.id());
        assertEquals("Alice", dto.firstName());
        assertEquals("Smith", dto.lastName());
        assertEquals(2, dto.specialties().size());
        assertEquals(10, dto.specialties().get(0).id());
        assertEquals("radiology", dto.specialties().get(0).name());
    }
}
