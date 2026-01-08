package org.springframework.samples.petclinic.customers.model.owner;

import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerCreateDTO;
import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerDTO;
import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class OwnerEntityMapper {

    public Owner map(final Owner owner, final OwnerCreateDTO request) {
        owner.setAddress(request.address());
        owner.setCity(request.city());
        owner.setTelephone(request.telephone());
        owner.setFirstName(request.firstName());
        owner.setLastName(request.lastName());
        return owner;
    }

    public Owner map(final Owner owner, final OwnerUpdateDTO request) {
        owner.setAddress(request.address());
        owner.setCity(request.city());
        owner.setTelephone(request.telephone());
        owner.setFirstName(request.firstName());
        owner.setLastName(request.lastName());
        return owner;
    }

    public OwnerDTO map(final Owner owner) {
        return new OwnerDTO(
            owner.getId(),
            owner.getFirstName(),
            owner.getLastName(),
            owner.getAddress(),
            owner.getCity(),
            owner.getTelephone(),
            owner.getPetsInternal()
        );
    }
}
