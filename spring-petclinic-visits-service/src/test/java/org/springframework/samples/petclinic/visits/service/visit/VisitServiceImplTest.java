package org.springframework.samples.petclinic.visits.service.visit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.VisitRepository;
import org.springframework.samples.petclinic.visits.model.visit.DTO.VisitPostDTO;
import org.springframework.samples.petclinic.visits.service.pet.PetDetails;
import org.springframework.samples.petclinic.visits.service.pet.PetServiceClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class VisitServiceImplTest {
    private PetServiceClient petServiceClient;
    private VisitRepository visitRepository;
    private VisitServiceImpl visitService;

    @BeforeEach
    void setup() {
        this.visitRepository = mock(VisitRepository.class);
        this.petServiceClient = mock(PetServiceClient.class);
        this.visitService = new VisitServiceImpl(
            visitRepository,
            petServiceClient
        );
    }

    @Test
    void createVisitWithActivePet_savesWithPetIdAndDescription() {
        when(petServiceClient.getPet(123))
            .thenReturn(Mono.just(PetDetails.
                PetDetailsBuilder.
                aPetDetails().
                id(123).deleted(false).build()));

        when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> {
            Visit v = invocation.getArgument(0);
            v.setId(42);
            return v;
        });

        final var dto = new VisitPostDTO(null, "Checkup", null);
        final Visit saved = visitService.createVisit(123, dto);

        ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
        verify(visitRepository).save(captor.capture());
        final Visit toSave = captor.getValue();
        assertThat(toSave.getPetId()).isEqualTo(123);
        assertThat(toSave.getDescription()).isEqualTo("Checkup");
        assertThat(saved.getId()).isEqualTo(42);
    }

    @Test
    void createVisitWithDeletedPet_ThrowIllegalState() {
        when(petServiceClient.getPet(123))
            .thenReturn(Mono.just(PetDetails.
                PetDetailsBuilder.
                aPetDetails().
                id(123).deleted(true).build()));

        var dto = new VisitPostDTO(null, "Checkup", null);

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> visitService.createVisit(123, dto)
        );

        assertEquals("Cannot create visit for deleted pet", ex.getMessage());
        verify(visitRepository, never()).save(any());
    }

    @Test
    void getVisitsByPetId_delegatesToRepository() {
        final Visit v = new Visit();
        v.setId(1);
        v.setPetId(99);
        when(visitRepository.findByPetId(99)).thenReturn(List.of(v));

        final List<Visit> result = visitService.getVisitsByPetId(99);
        assertThat(result).hasSize(1);
        verify(visitRepository).findByPetId(99);
    }

    @Test
    void getVisitsByPetIds_delegatesToRepository() {
        when(visitRepository.findByPetIdIn(Arrays.asList(1, 2))).thenReturn(List.of(new Visit(), new Visit()));
        final List<Visit> result = visitService.getVisitsByPetIds(Arrays.asList(1, 2));
        assertThat(result).hasSize(2);
        verify(visitRepository).findByPetIdIn(Arrays.asList(1, 2));
    }

    @Test
    void getVisitsByVetId_delegatesToRepository() {
        when(visitRepository.findByVetId(2)).thenReturn(List.of(new Visit()));
        final List<Visit> result = visitService.getVisitsByVetId(2);
        assertThat(result).hasSize(1);
        verify(visitRepository).findByVetId(2);
    }

    @Test
    void dateFilter_callsBetweenWhenFromAndToPresent() {
        java.util.Date from = new java.util.Date(0);
        java.util.Date to = new java.util.Date();
        when(visitRepository.findByVetIdAndDateBetween(2, from, to)).thenReturn(List.of(new Visit()));
        final List<Visit> result = visitService.getVisitsByVetIdWithDateFilter(2, from, to);
        assertThat(result).hasSize(1);
        verify(visitRepository).findByVetIdAndDateBetween(2, from, to);
    }

    @Test
    void deleteVisit_throwsOnPastOrNullDate() {
        final Visit v = new Visit();
        v.setId(1);
        v.setPetId(123);
        v.setDate(new java.util.Date(0));
        when(visitRepository.findById(1)).thenReturn(java.util.Optional.of(v));

        when(petServiceClient.getPet(123))
            .thenReturn(Mono.just(PetDetails.
                PetDetailsBuilder.
                aPetDetails().
                id(123).deleted(false).build()));

        try {
            visitService.deleteVisit(1);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).contains("future");
        }
        verify(visitRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteVisit_deletesFuture() {
        final Visit v = new Visit();
        v.setId(2);
        v.setPetId(123);
        v.setDate(new java.util.Date(System.currentTimeMillis() + 86400000));
        when(visitRepository.findById(2)).thenReturn(java.util.Optional.of(v));

        when(petServiceClient.getPet(123))
            .thenReturn(Mono.just(PetDetails.
                PetDetailsBuilder.
                aPetDetails().
                id(123).deleted(false).build()));

        visitService.deleteVisit(2);
        verify(visitRepository).deleteById(2);
    }

    @Test
    void deleteVisitOnDeletedPet_throws() {
        final Visit v = new Visit();
        v.setId(2);
        v.setPetId(123);
        v.setDate(new java.util.Date(System.currentTimeMillis() + 86400000));

        when(visitRepository.findById(2)).thenReturn(java.util.Optional.of(v));
        when(petServiceClient.getPet(123))
            .thenReturn(Mono.just(PetDetails.
                PetDetailsBuilder.
                aPetDetails().
                id(123).deleted(true).build()));

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> visitService.deleteVisit(2)
        );
        assertEquals("Cannot delete visit for deleted pet", ex.getMessage());
        verify(visitRepository, never()).deleteById(any());
    }
}
