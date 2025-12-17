package org.springframework.samples.petclinic.vets.service.specialty;

import org.springframework.samples.petclinic.vets.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.vets.model.specialty.Specialty;
import org.springframework.samples.petclinic.vets.repository.specialty.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Specialty getSpecialtyById(int specialtyId) {
        Optional<Specialty> optionalSpecialty = specialtyRepository.findById(specialtyId);
        if (optionalSpecialty.isEmpty()) {
            throw new ResourceNotFoundException("Specialty " + specialtyId + " not found");
        }
        return optionalSpecialty.get();
    }
}
