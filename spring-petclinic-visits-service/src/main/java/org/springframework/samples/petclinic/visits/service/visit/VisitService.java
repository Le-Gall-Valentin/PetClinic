package org.springframework.samples.petclinic.visits.service.visit;

import org.springframework.samples.petclinic.visits.model.Visit;
import java.util.List;

public interface VisitService {
    Visit createVisit(int petId, org.springframework.samples.petclinic.visits.model.visit.DTO.VisitPostDTO visitPostDTO);
    List<Visit> getVisitsByPetId(int petId);
    List<Visit> getVisitsByPetIds(java.util.List<Integer> petIds);
    List<Visit> getVisitsByVetId(int vetId);
    java.util.List<Visit> getVisitsByVetIdWithDateFilter(int vetId, java.util.Date from, java.util.Date to);
    void deleteVisit(int visitId);
}
