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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {
    @Mock
    private SpecialtyRepository specialtyRepository;
    @InjectMocks
    private SpecialtyServiceImpl service;

    @Test
    void getAllSpecialties_delegates() {
        when(specialtyRepository.findAll()).thenReturn(List.of(new Specialty()));
        assertThat(service.getAllSpecialties()).hasSize(1);
        verify(specialtyRepository).findAll();
    }

    @Test
    void getSpecialtyById_notFound_throws() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getSpecialtyById(99));
    }
}
