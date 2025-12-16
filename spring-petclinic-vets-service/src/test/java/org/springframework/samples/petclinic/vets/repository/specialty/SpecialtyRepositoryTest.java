package org.springframework.samples.petclinic.vets.repository.specialty;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.samples.petclinic.vets.model.specialty.Specialty;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository repository;

    @Test
    void findAll_hasInitialData() {
        List<Specialty> all = repository.findAll();
        assertFalse(all.isEmpty());
    }

    @Test
    void save_and_findById() {
        Specialty s = new Specialty();
        s.setName("dermatology");
        Specialty saved = repository.save(s);
        assertNotNull(saved.getId());
        Specialty fetched = repository.findById(saved.getId()).orElseThrow();
        assertEquals("dermatology", fetched.getName());
    }
}
