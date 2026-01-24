package org.springframework.samples.petclinic.vets.service.vet;

import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.vet.Vet;

import java.util.List;

public interface VetService {
    List<Vet> getAllVets();

    Vet getVetById(int vetId);

    Vet addVet(VetPostDTO vetPostDTO);

    void updateVet(int vetId, VetPostDTO vetPostDTO);

    void deleteVet(int vetId);
}
