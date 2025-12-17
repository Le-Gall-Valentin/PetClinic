package org.springframework.samples.petclinic.visits.model.visit.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import java.util.Date;

public record VisitPostDTO(
    @JsonFormat(pattern = "yyyy-MM-dd") Date date,
    @Size(max = 8192) String description,
    Integer vetId
) {}
