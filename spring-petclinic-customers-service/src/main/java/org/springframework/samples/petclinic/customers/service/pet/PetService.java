package org.springframework.samples.petclinic.customers.service.pet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.customers.exception.BadRequestException;
import org.springframework.samples.petclinic.customers.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.customers.model.owner.Owner;
import org.springframework.samples.petclinic.customers.model.pet.Pet;
import org.springframework.samples.petclinic.customers.model.pettype.PetType;
import org.springframework.samples.petclinic.customers.repository.OwnerRepository;
import org.springframework.samples.petclinic.customers.repository.PetRepository;
import org.springframework.samples.petclinic.customers.repository.PetTypeRepository;
import org.springframework.samples.petclinic.customers.service.visit.VisitsServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class PetService {
    private static final Logger log = LoggerFactory.getLogger(PetService.class);
    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;
    private final PetTypeRepository petTypeRepository;
    private final VisitsServiceClient visitsServiceClient;

    public PetService(PetRepository petRepository, OwnerRepository ownerRepository,
                      PetTypeRepository petTypeRepository, VisitsServiceClient visitsServiceClient) {
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
        this.petTypeRepository = petTypeRepository;
        this.visitsServiceClient = visitsServiceClient;
    }

    @Transactional(readOnly = true)
    public List<PetType> getAllPetTypes() {
        return petTypeRepository.findAll();
    }

    @Transactional()
    public Pet createPet(final Pet pet, final int ownerId, final int petTypeId) {
        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner " + ownerId + " not found"));

        PetType petType = getPetTypeById(petTypeId);

        owner.addPet(pet);
        pet.setOwner(owner);
        pet.setType(petType);

        log.info("Saving pet {}", pet);
        return petRepository.save(pet);
    }

    @Transactional()
    public Pet updatePet(final int petId, final Pet pet, final int petTypeId) {
        Pet updatedPet = findPetById(petId);

        if (updatedPet.getDeletedAt() != null) {
            throw new BadRequestException("Cannot update deleted pet");
        }

        PetType petType = getPetTypeById(petTypeId);

        updatedPet.setName(pet.getName());
        updatedPet.setBirthDate(pet.getBirthDate());
        updatedPet.setType(petType);

        log.info("Updating pet {}", updatedPet);
        return petRepository.save(updatedPet);
    }

    @Transactional(readOnly = true)
    public PetType getPetTypeById(final int petTypeId) {
        return petTypeRepository.findById(petTypeId)
            .orElseThrow(() -> new ResourceNotFoundException("PetType " + petTypeId + " not found"));
    }

    @Transactional(readOnly = true)
    public Pet findPetById(final int petId) {
        return petRepository.findPetById(petId)
            .orElseThrow(() -> new ResourceNotFoundException("Pet " + petId + " not found"));
    }

    @Transactional()
    public void deletePet(final int petId) {
        Boolean hasFutureVisits = visitsServiceClient.getVisitsForPets(Collections.singletonList(petId))
            .map(visits -> visits.getItems().stream()
                .filter(v -> v.getPetId() == petId)
                .anyMatch(v -> LocalDate.parse(v.getDate()).isAfter(LocalDate.now())))
            .block();

        if (Boolean.TRUE.equals(hasFutureVisits)) {
            throw new BadRequestException("Cannot delete pet with future visits");
        }

        Pet deletedPet = findPetById(petId);
        if (deletedPet.getDeletedAt() == null) {
            deletedPet.delete();
        }
        log.info("Deleting pet {}", deletedPet);
        petRepository.save(deletedPet);
    }
}
