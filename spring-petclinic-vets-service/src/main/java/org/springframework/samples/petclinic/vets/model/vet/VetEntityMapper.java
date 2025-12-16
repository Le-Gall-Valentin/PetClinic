package org.springframework.samples.petclinic.vets.model.vet;

import org.springframework.samples.petclinic.vets.model.vet.DTO.VetDTO;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;
import org.springframework.stereotype.Component;

@Component
public class VetEntityMapper {
    public Vet map(final Vet response, final VetPostDTO request) {
        response.setFirstName(request.firstName());
        response.setLastName(request.lastName());
        return response;
    }

    public VetDTO map(final Vet vet) {
        return new VetDTO(vet.getId(), vet.getFirstName(), vet.getLastName(), vet.getSpecialties());
    }
}
