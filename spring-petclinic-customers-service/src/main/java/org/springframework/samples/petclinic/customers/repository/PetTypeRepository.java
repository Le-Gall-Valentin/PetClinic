package org.springframework.samples.petclinic.customers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.customers.model.pettype.PetType;

public interface PetTypeRepository extends JpaRepository<PetType, Integer> {
}
