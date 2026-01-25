package org.springframework.samples.petclinic.customers.service.owner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.petclinic.customers.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.customers.model.owner.Owner;
import org.springframework.samples.petclinic.customers.repository.OwnerRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {
    @Mock
    private OwnerRepository ownerRepository;
    @InjectMocks
    private OwnerService ownerService;

    @Test
    void createOwner_saves() {
        Owner o = new Owner();
        when(ownerRepository.save(o)).thenReturn(o);
        Owner saved = ownerService.createOwner(o);
        assertSame(o, saved);
        verify(ownerRepository).save(o);
    }

    @Test
    void findOwner_returns_or_throws() {
        Owner o = new Owner();
        when(ownerRepository.findById(7)).thenReturn(Optional.of(o));
        assertSame(o, ownerService.findOwner(7));

        when(ownerRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ownerService.findOwner(99));
    }

    @Test
    void getAllOwners_delegatesToRepository() {
        when(ownerRepository.searchOwners("", "", "", "", PageRequest.of(0,20)))
            .thenReturn(new PageImpl<>(List.of(new Owner())));
        assertEquals(1, ownerService.getAllOwners("", "", "", "", PageRequest.of(0,20)).getTotalElements());
        verify(ownerRepository).searchOwners("", "", "", "", PageRequest.of(0,20));
    }

    @Test
    void updateOwner_updates_and_saves() {
        Owner existing = new Owner();
        existing.setFirstName("A");
        existing.setLastName("B");
        existing.setAddress("addr");
        existing.setCity("city");
        existing.setTelephone("000");
        when(ownerRepository.findById(5)).thenReturn(Optional.of(existing));

        Owner src = new Owner();
        src.setFirstName("X");
        src.setLastName("Y");
        src.setAddress("addr2");
        src.setCity("city2");
        src.setTelephone("111");
        when(ownerRepository.save(existing)).thenReturn(existing);

        Owner result = ownerService.updateOwner(5, src);
        assertEquals("X", result.getFirstName());
        assertEquals("Y", result.getLastName());
        assertEquals("addr2", result.getAddress());
        assertEquals("city2", result.getCity());
        assertEquals("111", result.getTelephone());
        verify(ownerRepository).save(existing);
    }
}
