package org.springframework.samples.petclinic.customers.model.owner.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

public record OwnerCreateDTO(@NotBlank String firstName,
                             @NotBlank String lastName,
                             @NotBlank String address,
                             @NotBlank String city,
                             @NotBlank
                             @Digits(fraction = 0, integer = 12)
                             String telephone
) {
}
