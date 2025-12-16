package org.springframework.samples.petclinic.vets.service.vet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.vets.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.vet.Vet;
import org.springframework.samples.petclinic.vets.model.vet.VetEntityMapper;
import org.springframework.samples.petclinic.vets.repository.vet.VetRepository;
import org.springframework.samples.petclinic.vets.service.specialty.SpecialtyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VetServiceImpl implements VetService {
    private static final Logger log = LoggerFactory.getLogger(VetServiceImpl.class);

    private final VetRepository vetRepository;
    private final SpecialtyService specialtyService;
    private final VetEntityMapper vetEntityMapper;

    public VetServiceImpl(VetRepository vetRepository, VetEntityMapper vetEntityMapper, SpecialtyService specialtyService) {
        this.vetRepository = vetRepository;
        this.specialtyService = specialtyService;
        this.vetEntityMapper = vetEntityMapper;
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
        log.info("Saving vet {}", vetModel);
        vetRepository.save(vetModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Vet getVetById(int vetId) {
        return vetRepository.findById(vetId).orElseThrow(() -> new ResourceNotFoundException("Vet " + vetId + " not found"));
    }
}
