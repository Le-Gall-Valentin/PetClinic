package org.springframework.samples.petclinic.visits.controller.visit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import io.micrometer.core.annotation.Timed;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.visits.model.Visit;
import org.springframework.samples.petclinic.visits.model.visit.DTO.VisitDTO;
import org.springframework.samples.petclinic.visits.model.visit.DTO.VisitPostDTO;
import org.springframework.samples.petclinic.visits.model.visit.VisitEntityMapper;
import org.springframework.samples.petclinic.visits.service.visit.VisitService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Validated
@Timed("petclinic.visit")
public class VisitController {
    private final VisitService visitService;
    private final VisitEntityMapper visitEntityMapper;

    public VisitController(VisitService visitService, VisitEntityMapper visitEntityMapper) {
        this.visitService = visitService;
        this.visitEntityMapper = visitEntityMapper;
    }

    @PostMapping("owners/*/pets/{petId}/visits")
    public ResponseEntity<VisitDTO> create(
        @Valid @RequestBody VisitPostDTO visitPostDTO,
        @PathVariable("petId") @Min(1) int petId) {
        final Visit saved = visitService.createVisit(petId, visitPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(visitEntityMapper.map(saved));
    }

    @GetMapping("owners/*/pets/{petId}/visits")
    public ResponseEntity<List<VisitDTO>> read(@PathVariable("petId") @Min(1) int petId) {
        return ResponseEntity.ok(visitService.getVisitsByPetId(petId).stream().map(visitEntityMapper::map).toList());
    }

    @GetMapping("pets/visits")
    public ResponseEntity<Visits> read(@RequestParam("petId") List<Integer> petIds) {
        final List<VisitDTO> items = visitService.getVisitsByPetIds(petIds).stream().map(visitEntityMapper::map).toList();
        return ResponseEntity.ok(new Visits(items));
    }

    @GetMapping("vets/{vetId}/visits")
    public ResponseEntity<List<VisitDTO>> readByVet(
        @PathVariable("vetId") @Min(1) int vetId,
        @RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
        @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to
    ) {
        final List<VisitDTO> items = visitService
            .getVisitsByVetIdWithDateFilter(vetId, from, to)
            .stream().map(visitEntityMapper::map).toList();
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("visits/{visitId}")
    public ResponseEntity<Void> delete(@PathVariable("visitId") @Min(1) int visitId) {
        visitService.deleteVisit(visitId);
        return ResponseEntity.noContent().build();
    }

    public record Visits(List<VisitDTO> items) {}
}
