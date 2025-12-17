package org.springframework.samples.petclinic.visits.model.visit;

import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.visit.DTO.VisitDTO;
import org.springframework.samples.petclinic.visits.model.visit.DTO.VisitPostDTO;
import org.springframework.stereotype.Component;

@Component
public class VisitEntityMapper {
    public Visit map(final Visit response, final VisitPostDTO request) {
        response.setDate(request.date());
        response.setDescription(request.description());
        response.setVetId(request.vetId());
        return response;
    }

    public VisitDTO map(final Visit visit) {
        return new VisitDTO(visit.getId(), visit.getDate(), visit.getDescription(), visit.getPetId(), visit.getVetId());
    }
}
