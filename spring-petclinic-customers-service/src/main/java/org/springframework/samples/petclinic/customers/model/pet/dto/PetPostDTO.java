package org.springframework.samples.petclinic.customers.model.pet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record PetPostDTO(
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date birthDate,
    @Size(min = 1)
    String name,
    int typeId
) {
}
