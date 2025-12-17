package org.springframework.samples.petclinic.vets.service.specialty;

import org.springframework.samples.petclinic.vets.model.specialty.Specialty;

import java.util.List;

public interface SpecialtyService {
    List<Specialty> getAllSpecialties();

    Specialty getSpecialtyById(int specialtyId);
}
