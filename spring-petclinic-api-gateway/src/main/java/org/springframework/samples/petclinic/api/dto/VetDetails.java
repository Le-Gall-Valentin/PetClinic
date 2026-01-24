package org.springframework.samples.petclinic.api.dto;

import java.util.List;

public record VetDetails(
    Integer id,
    String firstName,
    String lastName,
    List<SpecialtyDetails> specialties,
    boolean deleted
) {
}
