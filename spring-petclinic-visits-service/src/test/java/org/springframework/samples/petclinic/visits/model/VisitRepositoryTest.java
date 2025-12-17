package org.springframework.samples.petclinic.visits.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VisitRepositoryTest {
    @Autowired
    private VisitRepository visitRepository;

    @Test
    void findByPetId_returnsData() {
        final List<Visit> visits = visitRepository.findByPetId(7);
        assertThat(visits).isNotEmpty();
        assertThat(visits.get(0).getPetId()).isEqualTo(7);
    }

    @Test
    void findByPetIdIn_returnsData() {
        final List<Visit> visits = visitRepository.findByPetIdIn(java.util.Arrays.asList(7, 8));
        assertThat(visits).isNotEmpty();
        assertThat(visits.stream().map(Visit::getPetId).toList()).containsAnyOf(7, 8);
    }

    @Test
    void findByVetId_returnsData() {
        final List<Visit> visits = visitRepository.findByVetId(2);
        assertThat(visits).isNotEmpty();
        assertThat(visits.stream().map(Visit::getVetId).toList()).allMatch(id -> id != null && id == 2);
    }
}
