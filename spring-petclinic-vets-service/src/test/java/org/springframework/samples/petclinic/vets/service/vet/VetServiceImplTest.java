package org.springframework.samples.petclinic.vets.service.vet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.vets.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.vets.model.specialty.Specialty;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.vet.Vet;
import org.springframework.samples.petclinic.vets.model.vet.VetEntityMapper;
import org.springframework.samples.petclinic.vets.repository.vet.VetRepository;
import org.springframework.samples.petclinic.vets.service.specialty.SpecialtyService;
import org.springframework.samples.petclinic.vets.service.visit.VisitsServiceClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VetServiceImplTest {
    @Mock
    private VetRepository vetRepository;
    @Mock
    private SpecialtyService specialtyService;
    @Mock
    private VetEntityMapper vetEntityMapper;
    @Mock
    private VisitsServiceClient visitsServiceClient;

    @InjectMocks
    private VetServiceImpl service;

    @Test
    void getAllVets_delegates() {
        when(vetRepository.findAll()).thenReturn(List.of(new Vet()));
        assertThat(service.getAllVets()).hasSize(1);
        verify(vetRepository).findAll();
    }

    @Test
    void addVet_maps_addsSpecialties_andSaves() {
        VetPostDTO dto = new VetPostDTO("A","B", List.of(1,2));
        Vet vet = new Vet();
        when(vetEntityMapper.map(any(Vet.class), eq(dto))).thenReturn(vet);
        when(specialtyService.getSpecialtyById(anyInt())).thenReturn(new Specialty());
        when(vetRepository.save(vet)).thenReturn(vet);
        Vet saved = service.addVet(dto);
        assertThat(saved).isSameAs(vet);
        verify(vetRepository).save(vet);
    }

    @Test
    void updateVet_notFound_throws() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateVet(99, new VetPostDTO("A","B", List.of())));
    }

    @Test
    void updateVet_updatesSpecialties_andSaves() {
        Vet vet = new Vet();
        when(vetRepository.findById(7)).thenReturn(Optional.of(vet));
        VetPostDTO dto = new VetPostDTO("A","B", List.of(1));
        when(specialtyService.getSpecialtyById(1)).thenReturn(new Specialty());
        service.updateVet(7, dto);
        verify(vetEntityMapper).map(vet, dto);
        verify(vetRepository).save(vet);
    }

    @Test
    void getVetById_notFound_throws() {
        when(vetRepository.findById(77)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getVetById(77));
    }

    @Test
    void deleteVet_throws_whenHasFutureVisits() {
        Vet vet = new Vet();
        when(vetRepository.findById(5)).thenReturn(Optional.of(vet));
        when(visitsServiceClient.getVisitsByVetFrom(5, LocalDate.now().toString())).thenReturn(Mono.just(java.util.List.of(new VisitsServiceClient.VisitDTO(1,1,5, LocalDate.now().plusDays(1).toString(), "desc"))));
        assertThrows(IllegalStateException.class, () -> service.deleteVet(5));
    }

    @Test
    void deleteVet_softDelete_andSave_whenNoFutureVisits() {
        Vet vet = new Vet();
        when(vetRepository.findById(6)).thenReturn(Optional.of(vet));
        when(visitsServiceClient.getVisitsByVetFrom(eq(6), anyString())).thenReturn(Mono.just(java.util.List.of()));
        when(vetRepository.save(vet)).thenReturn(vet);
        service.deleteVet(6);
        assertThat(vet.getDeletedAt()).isNotNull();
        verify(vetRepository).save(vet);
    }
}
