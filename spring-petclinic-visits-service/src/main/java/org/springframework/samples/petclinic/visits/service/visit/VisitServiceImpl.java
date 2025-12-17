package org.springframework.samples.petclinic.visits.service.visit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VisitServiceImpl implements VisitService {
    private static final Logger log = LoggerFactory.getLogger(VisitServiceImpl.class);

    private final VisitRepository visitRepository;

    public VisitServiceImpl(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    @Transactional
    public Visit createVisit(int petId, org.springframework.samples.petclinic.visits.model.visit.DTO.VisitPostDTO visitPostDTO) {
        final Visit visit = new Visit();
        visit.setPetId(petId);
        visit.setDate(visitPostDTO.date());
        visit.setDescription(visitPostDTO.description());
        visit.setVetId(visitPostDTO.vetId());
        log.info("Saving visit {}", visit);
        return visitRepository.save(visit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Visit> getVisitsByPetId(int petId) {
        return visitRepository.findByPetId(petId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Visit> getVisitsByPetIds(List<Integer> petIds) {
        return visitRepository.findByPetIdIn(petIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Visit> getVisitsByVetId(int vetId) {
        return visitRepository.findByVetId(vetId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Visit> getVisitsByVetIdWithDateFilter(int vetId, Date from, Date to) {
        if (from != null && to != null) {
            return visitRepository.findByVetIdAndDateBetween(vetId, from, to);
        } else if (from != null) {
            return visitRepository.findByVetIdAndDateGreaterThanEqual(vetId, from);
        } else if (to != null) {
            return visitRepository.findByVetIdAndDateLessThanEqual(vetId, to);
        }
        return visitRepository.findByVetId(vetId);
    }

    @Override
    @Transactional
    public void deleteVisit(int visitId) {
        final Optional<Visit> opt = visitRepository.findById(visitId);
        if (opt.isEmpty()) {
            throw new jakarta.persistence.EntityNotFoundException("Visit not found: " + visitId);
        }
        final Visit visit = opt.get();
        final Date now = new Date();
        if (visit.getDate() == null || !visit.getDate().after(now)) {
            throw new IllegalStateException("Only future visits can be deleted");
        }
        log.info("Deleting visit {}", visitId);
        visitRepository.deleteById(visitId);
    }
}
