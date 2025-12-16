package org.springframework.samples.petclinic.vets.service.vet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.vets.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.vets.model.specialty.Specialty;
import org.springframework.samples.petclinic.vets.model.specialty.SpecialtyMapper;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.vet.Vet;
import org.springframework.samples.petclinic.vets.model.vet.VetEntityMapper;
import org.springframework.samples.petclinic.vets.repository.vet.VetRepository;
import org.springframework.samples.petclinic.vets.service.specialty.SpecialtyService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;
    @Mock
    private SpecialtyService specialtyService;

    @InjectMocks
    private VetServiceImpl service;

    @BeforeEach
    void setup() {
        VetEntityMapper mapper = new VetEntityMapper(new SpecialtyMapper());
        service = new VetServiceImpl(vetRepository, mapper, specialtyService);
    }

    @Test
    void getAllVets_returnsList() {
        when(vetRepository.findAll()).thenReturn(List.of(new Vet(), new Vet()));
        List<Vet> vets = service.getAllVets();
        assertEquals(2, vets.size());
        verify(vetRepository).findAll();
    }

    @Test
    void addVet_mapsNames_addsSpecialties_andSaves() {
        VetPostDTO dto = new VetPostDTO("John", "Doe", List.of(10, 11));
        Specialty s1 = new Specialty();
        s1.setId(10);
        Specialty s2 = new Specialty();
        s2.setId(11);
        when(specialtyService.getSpecialtyById(10)).thenReturn(s1);
        when(specialtyService.getSpecialtyById(11)).thenReturn(s2);
        when(vetRepository.save(any(Vet.class))).thenAnswer(inv -> inv.getArgument(0, Vet.class));

        Vet saved = service.addVet(dto);

        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals(2, saved.getNrOfSpecialties());
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void updateVet_updatesExisting_andSaves() {
        Vet existing = new Vet();
        existing.setId(1);
        existing.setFirstName("A");
        existing.setLastName("B");
        when(vetRepository.findById(1)).thenReturn(Optional.of(existing));

        VetPostDTO dto = new VetPostDTO("Alice", "Smith", List.of());
        service.updateVet(1, dto);

        assertEquals("Alice", existing.getFirstName());
        assertEquals("Smith", existing.getLastName());
        verify(vetRepository).save(existing);
    }

    @Test
    void updateVet_throws404_whenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateVet(99, new VetPostDTO("x", "y", List.of())));
    }

    @Test
    void getVetById_returnsEntity() {
        Vet v = new Vet();
        v.setId(7);
        when(vetRepository.findById(7)).thenReturn(Optional.of(v));

        Vet result = service.getVetById(7);
        assertEquals(7, result.getId());
    }

    @Test
    void getVetById_throws404_whenMissing() {
        when(vetRepository.findById(7)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getVetById(7));
    }
}
