package org.springframework.samples.petclinic.vets.service.vet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.vets.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.vet.Vet;
import org.springframework.samples.petclinic.vets.model.vet.VetEntityMapper;
import org.springframework.samples.petclinic.vets.repository.vet.VetRepository;
import org.springframework.samples.petclinic.vets.service.specialty.SpecialtyService;
import org.springframework.samples.petclinic.vets.service.visit.VisitsServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class VetServiceImpl implements VetService {
    private static final Logger log = LoggerFactory.getLogger(VetServiceImpl.class);

    private final VetRepository vetRepository;
    private final SpecialtyService specialtyService;
    private final VetEntityMapper vetEntityMapper;
    private final VisitsServiceClient visitsServiceClient;

    public VetServiceImpl(VetRepository vetRepository, VetEntityMapper vetEntityMapper, SpecialtyService specialtyService, VisitsServiceClient visitsServiceClient) {
        this.vetRepository = vetRepository;
        this.specialtyService = specialtyService;
        this.vetEntityMapper = vetEntityMapper;
        this.visitsServiceClient = visitsServiceClient;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Vet> getAllVets() {
        return vetRepository.findAll();
    }

    @Override
    @Transactional
    public Vet addVet(VetPostDTO vetPostDTO) {
        final Vet vetModel = vetEntityMapper.map(new Vet(), vetPostDTO);
        log.info("Adding vet {}", vetModel);
        vetPostDTO.specialties().forEach(specialtyId -> vetModel.addSpecialty(specialtyService.getSpecialtyById(specialtyId)));

        return vetRepository.save(vetModel);
    }

    @Override
    @Transactional
    public void updateVet(int vetId, VetPostDTO vetPostDTO) {
        final Vet vetModel = vetRepository.findById(vetId).orElseThrow(() -> new ResourceNotFoundException("Vet " + vetId + " not found"));

        vetEntityMapper.map(vetModel, vetPostDTO);
        final java.util.Set<org.springframework.samples.petclinic.vets.model.specialty.Specialty> newSpecialties = new java.util.HashSet<>();
        if (vetPostDTO.specialties() != null) {
            vetPostDTO.specialties().forEach(specialtyId -> newSpecialties.add(specialtyService.getSpecialtyById(specialtyId)));
        }
        vetModel.setSpecialties(newSpecialties);
        log.info("Saving vet {}", vetModel);
        vetRepository.save(vetModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Vet getVetById(int vetId) {
        return vetRepository.findById(vetId).orElseThrow(() -> new ResourceNotFoundException("Vet " + vetId + " not found"));
    }

    @Transactional
    public void deleteVet(int vetId) {
        final Vet vet = getVetById(vetId);
        final Boolean hasFutureVisits = visitsServiceClient
            .getVisitsByVetFrom(vetId, LocalDate.now().toString())
            .map(list -> !list.isEmpty())
            .block();
        if (Boolean.TRUE.equals(hasFutureVisits)) {
            throw new IllegalStateException("Cannot delete vet with future visits");
        }
        if (vet.getDeletedAt() == null) {
            vet.delete();
        }
        log.info("Soft deleting vet {}", vetId);
        vetRepository.save(vet);
    }
}
