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
package org.springframework.samples.petclinic.vets.controller.vet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetDTO;
import org.springframework.samples.petclinic.vets.model.vet.DTO.VetPostDTO;
import org.springframework.samples.petclinic.vets.model.vet.VetEntityMapper;
import org.springframework.samples.petclinic.vets.service.vet.VetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Maciej Szarlinski
 */
@RequestMapping("/vets")
@RestController
@Validated
public class VetController {
    private final VetService vetService;
    private final VetEntityMapper vetEntityMapper;

    VetController(VetService vetService, VetEntityMapper vetEntityMapper) {
        this.vetService = vetService;
        this.vetEntityMapper = vetEntityMapper;
    }


    @GetMapping
    public ResponseEntity<List<VetDTO>> showResourcesVetList() {
        return ResponseEntity.ok(vetService.getAllVets().stream().map(vetEntityMapper::map).toList());
    }

    @PostMapping
    public ResponseEntity<VetDTO> addNewVet(@RequestBody @Valid VetPostDTO vetPostDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vetEntityMapper.map(vetService.addVet(vetPostDTO)));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<Void> updateVet(@PathVariable("vetId") @Min(1) int vetId, @Valid @RequestBody VetPostDTO vetPostDTO) {
        vetService.updateVet(vetId, vetPostDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{vetId}")
    public ResponseEntity<VetDTO> findVet(@PathVariable("vetId") @Min(1) int vetId) {
        return ResponseEntity.ok(vetEntityMapper.map(vetService.getVetById(vetId)));
    }
}
