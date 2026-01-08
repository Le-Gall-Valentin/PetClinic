package org.springframework.samples.petclinic.customers.model.owner.dto;

import org.springframework.samples.petclinic.customers.model.pet.Pet;

import java.util.Set;

public record OwnerDTO(
    Integer id,
    String firstName,
    String lastName,
    String address,
    String city,
    String telephone,
    Set<Pet> pets
) {
}
