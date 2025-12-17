package org.springframework.samples.petclinic.visits.model.visit.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public record VisitDTO(
    Integer id,
    @JsonFormat(pattern = "yyyy-MM-dd") Date date,
    String description,
    int petId,
    Integer vetId
) {}
