package org.springframework.samples.petclinic.vets.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.vets.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.vets.model.DTO.VetDTO;
import org.springframework.samples.petclinic.vets.model.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.Vet;
import org.springframework.samples.petclinic.vets.model.VetRepository;
import org.springframework.samples.petclinic.vets.model.mapper.VetEntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VetServiceImpl implements VetService {
    private static final Logger log = LoggerFactory.getLogger(VetServiceImpl.class);

    private final VetRepository vetRepository;
    private final VetEntityMapper vetEntityMapper;

    public VetServiceImpl(VetRepository vetRepository, VetEntityMapper vetEntityMapper) {
        this.vetRepository = vetRepository;
        this.vetEntityMapper = vetEntityMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public List<VetDTO> getAllVets() {
        return vetRepository.findAll().stream().map(vetEntityMapper::map).toList();
    }

    @Override
    @Transactional
    public VetDTO addVet(VetPostDTO vetPostDTO) {
        final Vet vetModel = vetEntityMapper.map(new Vet(), vetPostDTO);
        log.info("Adding vet {}", vetModel.toString());
        return vetEntityMapper.map(vetRepository.save(vetModel));
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
    public VetDTO getVetById(int vetId) {
        return vetEntityMapper.map(vetRepository.findById(vetId).orElseThrow(() -> new ResourceNotFoundException("Vet " + vetId + " not found")));
    }
}
