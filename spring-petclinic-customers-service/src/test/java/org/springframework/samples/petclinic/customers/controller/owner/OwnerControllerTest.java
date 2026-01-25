package org.springframework.samples.petclinic.customers.controller.owner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.owner.Owner;
import org.springframework.samples.petclinic.customers.model.owner.OwnerEntityMapper;
import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerCreateDTO;
import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerDTO;
import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerUpdateDTO;
import org.springframework.samples.petclinic.customers.service.owner.OwnerService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
@ActiveProfiles("test")
class OwnerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private OwnerService ownerService;
    @MockitoBean
    private OwnerEntityMapper ownerEntityMapper;

    @Test
    void createOwner_returns201() throws Exception {
        Owner mapped = new Owner();
        mapped.setFirstName("John");
        mapped.setLastName("Doe");
        mapped.setAddress("addr");
        mapped.setCity("city");
        mapped.setTelephone("0000000000");
        Owner saved = new Owner();
        saved.setFirstName("John");
        saved.setLastName("Doe");
        saved.setAddress("addr");
        saved.setCity("city");
        saved.setTelephone("0000000000");
        when(ownerEntityMapper.map(any(Owner.class), any(OwnerCreateDTO.class))).thenReturn(mapped);
        when(ownerService.createOwner(mapped)).thenReturn(saved);
        when(ownerEntityMapper.map(saved)).thenReturn(new OwnerDTO(11, "John", "Doe", "addr", "city", "0000000000", java.util.Set.of()));

        String body = """
                {"firstName":"John","lastName":"Doe","address":"addr","city":"city","telephone":"0000000000"}
            """;
        mvc.perform(post("/owners").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/owners/11"))
            .andExpect(jsonPath("$.id").value(11));
    }

    @Test
    void findOwner_returns200() throws Exception {
        Owner owner = new Owner();
        when(ownerService.findOwner(5)).thenReturn(owner);
        when(ownerEntityMapper.map(owner)).thenReturn(new OwnerDTO(5, "A", "B", "addr", "city", "000", java.util.Set.of()));

        mvc.perform(get("/owners/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void findAll_returnsPage() throws Exception {
        Owner owner = new Owner();
        when(ownerService.getAllOwners("", "", "", "", org.springframework.data.domain.PageRequest.of(0, 20, org.springframework.data.domain.Sort.Direction.ASC, "id")))
            .thenReturn(new PageImpl<>(List.of(owner)));
        when(ownerEntityMapper.map(owner)).thenReturn(new OwnerDTO(3, "A", "B", "addr", "city", "000", java.util.Set.of()));

        mvc.perform(get("/owners"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(3));
    }

    @Test
    void updateOwner_returns200() throws Exception {
        Owner mapped = new Owner();
        mapped.setFirstName("X");
        mapped.setLastName("Y");
        mapped.setAddress("addr2");
        mapped.setCity("city2");
        mapped.setTelephone("111");
        Owner updated = new Owner();
        updated.setFirstName("X");
        updated.setLastName("Y");
        updated.setAddress("addr2");
        updated.setCity("city2");
        updated.setTelephone("111");
        when(ownerEntityMapper.map(any(Owner.class), any(OwnerUpdateDTO.class))).thenReturn(mapped);
        when(ownerService.updateOwner(9, mapped)).thenReturn(updated);
        when(ownerEntityMapper.map(updated)).thenReturn(new OwnerDTO(9, "X", "Y", "addr2", "city2", "111", java.util.Set.of()));

        String body = """
                {"firstName":"X","lastName":"Y","address":"addr2","city":"city2","telephone":"111"}
            """;
        mvc.perform(put("/owners/9").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(9))
            .andExpect(jsonPath("$.firstName").value("X"))
            .andExpect(jsonPath("$.lastName").value("Y"));
    }
}
