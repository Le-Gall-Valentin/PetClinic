package org.springframework.samples.petclinic.visits.model.visit;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.visit.DTO.VisitDTO;
import org.springframework.samples.petclinic.visits.model.visit.DTO.VisitPostDTO;

import static org.assertj.core.api.Assertions.assertThat;

class VisitEntityMapperTest {
    private final VisitEntityMapper mapper = new VisitEntityMapper();

    @Test
    void mapPostDtoToEntity() {
        final Visit entity = new Visit();
        final VisitPostDTO dto = new VisitPostDTO(null, "Desc", null);
        final Visit mapped = mapper.map(entity, dto);
        assertThat(mapped.getDescription()).isEqualTo("Desc");
        assertThat(mapped.getDate()).isNull();
    }

    @Test
    void mapEntityToDto() {
        final Visit entity = new Visit();
        entity.setId(7);
        entity.setPetId(77);
        entity.setDescription("D");
        final VisitDTO dto = mapper.map(entity);
        assertThat(dto.id()).isEqualTo(7);
        assertThat(dto.petId()).isEqualTo(77);
        assertThat(dto.description()).isEqualTo("D");
    }
}
