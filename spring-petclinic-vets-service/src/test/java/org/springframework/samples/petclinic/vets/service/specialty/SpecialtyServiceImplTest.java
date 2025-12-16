package org.springframework.samples.petclinic.vets.service.specialty;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.vets.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.vets.model.specialty.Specialty;
import org.springframework.samples.petclinic.vets.repository.specialty.SpecialtyRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository repository;

    @InjectMocks
    private SpecialtyServiceImpl service;

    @Test
    void getAllSpecialties_returnsList() {
        when(repository.findAll()).thenReturn(List.of(new Specialty(), new Specialty(), new Specialty()));
        assertEquals(3, service.getAllSpecialties().size());
    }

    @Test
    void getSpecialtyById_returnsSpecialty() {
        Specialty s = new Specialty();
        s.setId(9);
        when(repository.findById(9)).thenReturn(Optional.of(s));
        assertEquals(9, service.getSpecialtyById(9).getId());
    }

    @Test
    void getSpecialtyById_throws404_whenMissing() {
        when(repository.findById(42)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getSpecialtyById(42));
    }
}
