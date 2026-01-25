package org.springframework.samples.petclinic.visits.model.visit;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.visit.DTO.VisitDTO;
import org.springframework.samples.petclinic.visits.model.visit.DTO.VisitPostDTO;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class VisitEntityMapperTest {
    @Test
    void map_post_setsFields() {
        VisitEntityMapper mapper = new VisitEntityMapper();
        Visit v = new Visit();
        VisitPostDTO post = new VisitPostDTO(new Date(), "desc", 7);
        mapper.map(v, post);
        assertThat(v.getDescription()).isEqualTo("desc");
        assertThat(v.getVetId()).isEqualTo(7);
        assertThat(v.getDate()).isNotNull();
    }

    @Test
    void map_toDTO_includesFields() {
        VisitEntityMapper mapper = new VisitEntityMapper();
        Visit v = new Visit();
        v.setId(5);
        v.setPetId(10);
        v.setVetId(7);
        v.setDescription("check");
        v.setDate(new Date());
        VisitDTO dto = mapper.map(v);
        assertThat(dto.id()).isEqualTo(5);
        assertThat(dto.petId()).isEqualTo(10);
        assertThat(dto.vetId()).isEqualTo(7);
        assertThat(dto.description()).isEqualTo("check");
        assertThat(dto.date()).isNotNull();
    }
}
