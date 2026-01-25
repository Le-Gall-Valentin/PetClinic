package org.springframework.samples.petclinic.visits.service.visit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.VisitRepository;
import org.springframework.samples.petclinic.visits.service.pet.PetDetails;
import org.springframework.samples.petclinic.visits.service.pet.PetServiceClient;
import org.springframework.samples.petclinic.visits.service.vet.VetServiceClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceImplTest {
    @Mock
    private VisitRepository visitRepository;
    @Mock
    private PetServiceClient petServiceClient;
    @Mock
    private VetServiceClient vetServiceClient;
    @InjectMocks
    private VisitServiceImpl service;

    @Test
    void createVisit_throws_whenPetDeleted() {
        when(petServiceClient.getPet(10)).thenReturn(Mono.just(PetDetails.PetDetailsBuilder.aPetDetails().id(10).deleted(true).build()));
        assertThrows(IllegalStateException.class, () -> service.createVisit(10, new org.springframework.samples.petclinic.visits.model.visit.DTO.VisitPostDTO(new Date(), "desc", null)));
    }

    @Test
    void createVisit_throws_whenVetDeleted() {
        when(petServiceClient.getPet(11)).thenReturn(Mono.just(PetDetails.PetDetailsBuilder.aPetDetails().id(11).deleted(false).build()));
        when(vetServiceClient.getVet(7)).thenReturn(Mono.just(new VetServiceClient.VetDetails(7, "A", "B", java.util.List.of(), true)));
        assertThrows(IllegalStateException.class, () -> service.createVisit(11, new org.springframework.samples.petclinic.visits.model.visit.DTO.VisitPostDTO(new Date(), "desc", 7)));
    }

    @Test
    void getVisitsByVetIdWithDateFilter_branches() {
        Date from = new Date(System.currentTimeMillis() - 1000);
        Date to = new Date(System.currentTimeMillis() + 1000);
        when(visitRepository.findByVetIdAndDateBetween(2, from, to)).thenReturn(java.util.List.of(new Visit()));
        assertThat(service.getVisitsByVetIdWithDateFilter(2, from, to)).hasSize(1);

        when(visitRepository.findByVetIdAndDateGreaterThanEqual(2, from)).thenReturn(java.util.List.of(new Visit()));
        assertThat(service.getVisitsByVetIdWithDateFilter(2, from, null)).hasSize(1);

        when(visitRepository.findByVetIdAndDateLessThanEqual(2, to)).thenReturn(java.util.List.of(new Visit()));
        assertThat(service.getVisitsByVetIdWithDateFilter(2, null, to)).hasSize(1);
    }

    @Test
    void deleteVisit_notFound_throws() {
        when(visitRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> service.deleteVisit(99));
    }

    @Test
    void deleteVisit_throws_whenPetDeleted() {
        Visit v = new Visit();
        v.setId(100);
        v.setPetId(1);
        v.setDate(new Date(System.currentTimeMillis() + 86_400_000));
        when(visitRepository.findById(100)).thenReturn(Optional.of(v));
        when(petServiceClient.getPet(1)).thenReturn(Mono.just(PetDetails.PetDetailsBuilder.aPetDetails().id(1).deleted(true).build()));
        assertThrows(IllegalStateException.class, () -> service.deleteVisit(100));
    }

    @Test
    void deleteVisit_throws_whenVetDeleted() {
        Visit v = new Visit();
        v.setId(101);
        v.setPetId(1);
        v.setVetId(7);
        v.setDate(new Date(System.currentTimeMillis() + 86_400_000));
        when(visitRepository.findById(101)).thenReturn(Optional.of(v));
        when(petServiceClient.getPet(1)).thenReturn(Mono.just(PetDetails.PetDetailsBuilder.aPetDetails().id(1).deleted(false).build()));
        when(vetServiceClient.getVet(7)).thenReturn(Mono.just(new VetServiceClient.VetDetails(7, "A", "B", java.util.List.of(), true)));
        assertThrows(IllegalStateException.class, () -> service.deleteVisit(101));
    }

    @Test
    void deleteVisit_throws_whenDateNull_orPast() {
        Visit v1 = new Visit();
        v1.setId(102);
        v1.setPetId(1);
        when(visitRepository.findById(102)).thenReturn(Optional.of(v1));
        when(petServiceClient.getPet(1)).thenReturn(Mono.just(PetDetails.PetDetailsBuilder.aPetDetails().id(1).deleted(false).build()));
        assertThrows(IllegalStateException.class, () -> service.deleteVisit(102));

        Visit v2 = new Visit();
        v2.setId(103);
        v2.setPetId(1);
        v2.setDate(new Date(System.currentTimeMillis() - 86_400_000));
        when(visitRepository.findById(103)).thenReturn(Optional.of(v2));
        when(petServiceClient.getPet(1)).thenReturn(Mono.just(PetDetails.PetDetailsBuilder.aPetDetails().id(1).deleted(false).build()));
        assertThrows(IllegalStateException.class, () -> service.deleteVisit(103));
    }

    @Test
    void deleteVisit_deletes_whenFuture_andNotDeleted() {
        Visit v = new Visit();
        v.setId(104);
        v.setPetId(1);
        v.setDate(new Date(System.currentTimeMillis() + 86_400_000));
        when(visitRepository.findById(104)).thenReturn(Optional.of(v));
        when(petServiceClient.getPet(1)).thenReturn(Mono.just(PetDetails.PetDetailsBuilder.aPetDetails().id(1).deleted(false).build()));
        doNothing().when(visitRepository).deleteById(104);
        service.deleteVisit(104);
        verify(visitRepository).deleteById(104);
    }
}
