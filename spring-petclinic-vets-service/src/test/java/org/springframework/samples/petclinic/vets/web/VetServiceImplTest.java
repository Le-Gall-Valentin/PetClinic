package org.springframework.samples.petclinic.vets.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.vets.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.vets.model.DTO.VetDTO;
import org.springframework.samples.petclinic.vets.model.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.Vet;
import org.springframework.samples.petclinic.vets.model.VetRepository;
import org.springframework.samples.petclinic.vets.model.mapper.VetEntityMapper;
import org.springframework.samples.petclinic.vets.service.VetServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VetServiceImplTest {

    @Mock
    VetRepository vetRepository;

    @Mock
    VetEntityMapper vetEntityMapper;

    @InjectMocks
    VetServiceImpl vetService;

    @Test
    void getAllVets_shouldReturnMappedDtos() {
        Vet v1 = new Vet();
        Vet v2 = new Vet();
        VetDTO dto1 = new VetDTO(1, "Jean", "Marc");
        VetDTO dto2 = new VetDTO(2, "Alice", "Durand");

        given(vetRepository.findAll()).willReturn(List.of(v1, v2));
        given(vetEntityMapper.map(v1)).willReturn(dto1);
        given(vetEntityMapper.map(v2)).willReturn(dto2);

        List<VetDTO> result = vetService.getAllVets();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).id());
        assertEquals(2, result.get(1).id());
        verify(vetRepository).findAll();
        verify(vetEntityMapper).map(v1);
        verify(vetEntityMapper).map(v2);
    }

    @Test
    void addVet_shouldMapSaveAndReturnDto() {
        VetPostDTO input = new VetPostDTO("Jean", "Marc");

        Vet mappedEntity = new Vet();
        Vet savedEntity = new Vet();
        VetDTO savedDto = new VetDTO(1, "Jean", "Marc");

        // map(new Vet(), input) retourne une entité (celle qui sera save)
        given(vetEntityMapper.map(any(Vet.class), eq(input))).willReturn(mappedEntity);
        given(vetRepository.save(mappedEntity)).willReturn(savedEntity);
        given(vetEntityMapper.map(savedEntity)).willReturn(savedDto);

        VetDTO result = vetService.addVet(input);

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("Jean", result.firstName());
        assertEquals("Marc", result.lastName());

        verify(vetEntityMapper).map(any(Vet.class), eq(input));
        verify(vetRepository).save(mappedEntity);
        verify(vetEntityMapper).map(savedEntity);
    }

    @Test
    void updateVet_shouldThrowWhenVetNotFound() {
        VetPostDTO input = new VetPostDTO("Jean", "Marc");
        given(vetRepository.findById(42)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vetService.updateVet(42, input));

        verify(vetRepository).findById(42);
        verify(vetRepository, never()).save(any());
        verify(vetEntityMapper, never()).map(any(Vet.class), any(VetPostDTO.class));
    }

    @Test
    void updateVet_shouldMapOnExistingAndSave() {
        int id = 1;
        VetPostDTO input = new VetPostDTO("Jean", "Marc");

        Vet existing = new Vet();
        given(vetRepository.findById(id)).willReturn(Optional.of(existing));
        // le mapper modifie l'entité existante (side-effect) -> pas besoin de return ici
        given(vetRepository.save(existing)).willReturn(existing);

        vetService.updateVet(id, input);

        verify(vetRepository).findById(id);
        verify(vetEntityMapper).map(existing, input);
        verify(vetRepository).save(existing);
    }

    @Test
    void getVetById_shouldThrowWhenNotFound() {
        given(vetRepository.findById(99)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vetService.getVetById(99));

        verify(vetRepository).findById(99);
        verify(vetEntityMapper, never()).map(any(Vet.class));
    }

    @Test
    void getVetById_shouldReturnMappedDto() {
        Vet entity = new Vet();
        VetDTO dto = new VetDTO(1, "Jean", "Marc");

        given(vetRepository.findById(1)).willReturn(Optional.of(entity));
        given(vetEntityMapper.map(entity)).willReturn(dto);

        VetDTO result = vetService.getVetById(1);

        assertEquals(1, result.id());
        assertEquals("Jean", result.firstName());
        assertEquals("Marc", result.lastName());

        verify(vetRepository).findById(1);
        verify(vetEntityMapper).map(entity);
    }
}
