package org.springframework.samples.petclinic.vets.service;

import org.springframework.samples.petclinic.vets.model.DTO.VetDTO;
import org.springframework.samples.petclinic.vets.model.DTO.VetPostDTO;

import java.util.List;

public interface VetService {
    List<VetDTO> getAllVets();
    VetDTO getVetById(int vetId);
    VetDTO addVet(VetPostDTO vetPostDTO);
    void updateVet(int vetId, VetPostDTO vetPostDTO);
}
