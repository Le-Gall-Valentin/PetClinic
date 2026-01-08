package org.springframework.samples.petclinic.customers.service.owner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.customers.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.customers.model.owner.Owner;
import org.springframework.samples.petclinic.customers.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OwnerService {

    private static final Logger log = LoggerFactory.getLogger(OwnerService.class);
    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Transactional()
    public Owner createOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Transactional(readOnly = true)
    public Owner findOwner(int ownerId) {
        return ownerRepository.findById(ownerId).orElseThrow(
            () -> new ResourceNotFoundException("Owner " + ownerId + " not found")
        );
    }

    @Transactional(readOnly = true)
    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    @Transactional()
    public Owner updateOwner(int ownerId, final Owner owner) {
        Owner updatedOwner = updateOwner(findOwner(ownerId), owner);
        log.info("Updating owner {}", updatedOwner);
        return ownerRepository.save(updatedOwner);
    }

    private Owner updateOwner(final Owner destination, final Owner source) {
        destination.setAddress(source.getAddress());
        destination.setCity(source.getCity());
        destination.setTelephone(source.getTelephone());
        destination.setFirstName(source.getFirstName());
        destination.setLastName(source.getLastName());
        return destination;
    }
}
