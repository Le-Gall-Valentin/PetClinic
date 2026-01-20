/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.customers.controller.pet;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.customers.model.pet.Pet;
import org.springframework.samples.petclinic.customers.model.pet.PetEntityMapper;
import org.springframework.samples.petclinic.customers.model.pet.dto.PetDTO;
import org.springframework.samples.petclinic.customers.model.pet.dto.PetPostDTO;
import org.springframework.samples.petclinic.customers.model.pet.dto.PetUpdateDTO;
import org.springframework.samples.petclinic.customers.model.pettype.PetTypeEntityMapper;
import org.springframework.samples.petclinic.customers.model.pettype.dto.PetTypeDTO;
import org.springframework.samples.petclinic.customers.service.pet.PetService;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Maciej Szarlinski
 * @author Ramazan Sakin
 */
@RestController
@Timed("petclinic.pet")
class PetController {

    private final PetService petService;
    private final PetTypeEntityMapper petTypeEntityMapper;
    private final PetEntityMapper petEntityMapper;

    PetController(PetService petService, PetTypeEntityMapper petTypeEntityMapper,
                  PetEntityMapper petEntityMapper) {
        this.petService = petService;
        this.petTypeEntityMapper = petTypeEntityMapper;
        this.petEntityMapper = petEntityMapper;
    }

    @GetMapping("/petTypes")
    public ResponseEntity<List<PetTypeDTO>> getPetTypes() {
        return ResponseEntity.ok(petService.getAllPetTypes()
            .stream()
            .map(petTypeEntityMapper::map)
            .toList());
    }

    @PostMapping("/owners/{ownerId}/pets")
    public ResponseEntity<PetDTO> processCreationForm(
        @RequestBody PetPostDTO petPostDTO,
        @PathVariable("ownerId") @Min(1) int ownerId) {

        PetDTO createdPet = petEntityMapper.map(petService.createPet(
            petEntityMapper.map(new Pet(), petPostDTO),
            ownerId,
            petPostDTO.typeId()
        ));

        URI location = URI.create("/owners/*/pets/" + createdPet.id());
        return ResponseEntity.created(location).body(createdPet);
    }

    @PutMapping("/owners/*/pets/{petId}")
    public ResponseEntity<PetDTO> processUpdateForm(
        @RequestBody PetUpdateDTO petUpdateDTO,
        @PathVariable int petId
    ) {
        return ResponseEntity.ok(
            petEntityMapper.map(
                petService.updatePet(
                    petId,
                    petEntityMapper.map(new Pet(), petUpdateDTO), petUpdateDTO.typeId())
            )
        );
    }

    @GetMapping("owners/*/pets/{petId}")
    public ResponseEntity<PetDTO> findPet(@PathVariable("petId") int petId) {
        return ResponseEntity.ok(petEntityMapper.map(petService.findPetById(petId)));
    }

    @DeleteMapping("owners/*/pets/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable("petId") int petId) {
        petService.deletePet(petId);
        return ResponseEntity.noContent().build();
    }
}
