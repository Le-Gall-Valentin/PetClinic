package org.springframework.samples.petclinic.vets.repository.vet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.vets.model.specialty.Specialty;
import org.springframework.samples.petclinic.vets.model.vet.Vet;
import org.springframework.samples.petclinic.vets.repository.specialty.SpecialtyRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;
    @Autowired
    private SpecialtyRepository specialtyRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAll_hasInitialData() {
        List<Vet> all = vetRepository.findAll();
        assertFalse(all.isEmpty());
    }

    @Test
    void saveVet_withSpecialties_persistsJoin_andSortsSpecialties() {
        Specialty s1 = specialtyRepository.findById(1).orElseGet(() -> {
            Specialty s = new Specialty(); s.setName("radiology"); return entityManager.persistFlushFind(s);
        });
        Specialty s2 = specialtyRepository.findById(2).orElseGet(() -> {
            Specialty s = new Specialty(); s.setName("surgery"); return entityManager.persistFlushFind(s);
        });

        Vet v = new Vet();
        v.setFirstName("Jane");
        v.setLastName("Doe");
        v.addSpecialty(s2);
        v.addSpecialty(s1);

        Vet saved = vetRepository.save(v);
        assertNotNull(saved.getId());
        assertEquals(2, saved.getNrOfSpecialties());
        List<Specialty> specs = saved.getSpecialties();
        assertEquals("radiology", specs.get(0).getName());
        assertEquals("surgery", specs.get(1).getName());
    }
}
