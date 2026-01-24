package org.springframework.samples.petclinic.vets.model.vet;

import org.springframework.samples.petclinic.vets.model.vet.DTO.VetDTO;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.specialty.DTO.SpecialtyDTO;
import org.springframework.samples.petclinic.vets.model.specialty.SpecialtyMapper;
import org.springframework.stereotype.Component;

@Component
public class VetEntityMapper {
    private final SpecialtyMapper specialtyMapper;

    public VetEntityMapper(SpecialtyMapper specialtyMapper) {
        this.specialtyMapper = specialtyMapper;
    }

    public Vet map(final Vet response, final VetPostDTO request) {
        response.setFirstName(request.firstName());
        response.setLastName(request.lastName());
        return response;
    }

    public VetDTO map(final Vet vet) {
        final java.util.List<SpecialtyDTO> specialties = vet.getSpecialties().stream().map(specialtyMapper::map).toList();
        return new VetDTO(vet.getId(), vet.getFirstName(), vet.getLastName(), specialties, vet.getDeletedAt() != null);
    }
}
