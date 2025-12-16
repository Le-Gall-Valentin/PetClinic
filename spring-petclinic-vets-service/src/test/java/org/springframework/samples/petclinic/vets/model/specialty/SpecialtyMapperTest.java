package org.springframework.samples.petclinic.vets.model.specialty;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.vets.model.specialty.DTO.SpecialtyDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpecialtyMapperTest {

    private final SpecialtyMapper mapper = new SpecialtyMapper();

    @Test
    void map_toDto() {
        Specialty s = new Specialty();
        s.setId(5);
        s.setName("dentistry");

        SpecialtyDTO dto = mapper.map(s);

        assertNotNull(dto);
        assertEquals(5, dto.id());
        assertEquals("dentistry", dto.name());
    }
}
