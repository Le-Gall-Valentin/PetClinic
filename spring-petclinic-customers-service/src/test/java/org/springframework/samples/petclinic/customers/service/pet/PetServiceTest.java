package org.springframework.samples.petclinic.customers.service.pet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.customers.exception.BadRequestException;
import org.springframework.samples.petclinic.customers.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.customers.model.owner.Owner;
import org.springframework.samples.petclinic.customers.model.pet.Pet;
import org.springframework.samples.petclinic.customers.model.pettype.PetType;
import org.springframework.samples.petclinic.customers.repository.OwnerRepository;
import org.springframework.samples.petclinic.customers.repository.PetRepository;
import org.springframework.samples.petclinic.customers.repository.PetTypeRepository;
import org.springframework.samples.petclinic.customers.service.visit.VisitDetails;
import org.springframework.samples.petclinic.customers.service.visit.Visits;
import org.springframework.samples.petclinic.customers.service.visit.VisitsServiceClient;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {
    @Mock
    private PetRepository petRepository;
    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private PetTypeRepository petTypeRepository;
    @Mock
    private VisitsServiceClient visitsServiceClient;

    @InjectMocks
    private PetService petService;

    @Test
    void createPet_setsOwnerAndType_andSaves() {
        Owner owner = new Owner();
        PetType type = new PetType();
        type.setId(6);
        Pet pet = new Pet();
        pet.setName("Basil");
        when(ownerRepository.findById(2)).thenReturn(Optional.of(owner));
        when(petTypeRepository.findById(6)).thenReturn(Optional.of(type));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0, Pet.class));
        Pet saved = petService.createPet(pet, 2, 6);
        assertThat(saved.getOwner()).isSameAs(owner);
        assertThat(saved.getType()).isSameAs(type);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void createPet_throws_whenOwnerMissing() {
        when(ownerRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> petService.createPet(new Pet(), 99, 1));
    }

    @Test
    void updatePet_throws_whenDeleted() {
        Pet existing = new Pet();
        existing.setId(3);
        existing.delete();
        when(petRepository.findPetById(3)).thenReturn(Optional.of(existing));
        assertThrows(BadRequestException.class, () -> petService.updatePet(3, new Pet(), 6));
    }

    @Test
    void deletePet_throws_whenFutureVisits() {
        VisitDetails visit = new VisitDetails();
        visit.setPetId(4);
        visit.setDate(LocalDate.now().plusDays(1).toString());
        Visits visits = new Visits(java.util.List.of(visit));
        when(visitsServiceClient.getVisitsForPets(Collections.singletonList(4))).thenReturn(reactor.core.publisher.Mono.just(visits));
        assertThrows(BadRequestException.class, () -> petService.deletePet(4));
    }

    @Test
    void deletePet_softDeletes_whenNoFutureVisits() {
        Visits visits = new Visits(java.util.List.of());
        Pet pet = new Pet();
        pet.setId(5);
        org.springframework.samples.petclinic.customers.model.owner.Owner owner = new org.springframework.samples.petclinic.customers.model.owner.Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        org.springframework.samples.petclinic.customers.model.pettype.PetType type = new org.springframework.samples.petclinic.customers.model.pettype.PetType();
        type.setId(1);
        type.setName("cat");
        pet.setOwner(owner);
        pet.setType(type);
        when(visitsServiceClient.getVisitsForPets(Collections.singletonList(5))).thenReturn(reactor.core.publisher.Mono.just(visits));
        when(petRepository.findPetById(5)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0, Pet.class));
        petService.deletePet(5);
        assertThat(pet.getDeletedAt()).isNotNull();
        verify(petRepository).save(pet);
    }

    @Test
    void getPetTypeById_throws_whenMissing() {
        when(petTypeRepository.findById(77)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> petService.getPetTypeById(77));
    }

    @Test
    void findPetById_throws_whenMissing() {
        when(petRepository.findPetById(7)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> petService.findPetById(7));
    }
}
