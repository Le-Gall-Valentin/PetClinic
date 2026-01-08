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
package org.springframework.samples.petclinic.customers.controller.owner;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.customers.model.owner.Owner;
import org.springframework.samples.petclinic.customers.model.owner.OwnerEntityMapper;
import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerCreateDTO;
import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerDTO;
import org.springframework.samples.petclinic.customers.model.owner.dto.OwnerUpdateDTO;
import org.springframework.samples.petclinic.customers.service.owner.OwnerService;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Szarlinski
 */
@RequestMapping("/owners")
@RestController
@Timed("petclinic.owner")
class OwnerController {

    private final OwnerService ownerService;
    private final OwnerEntityMapper ownerEntityMapper;

    OwnerController(OwnerEntityMapper ownerEntityMapper, OwnerService ownerService) {
        this.ownerEntityMapper = ownerEntityMapper;
        this.ownerService = ownerService;
    }

    /**
     * Create Owner
     */
    @PostMapping
    public ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody OwnerCreateDTO ownerCreateDTO) {
        OwnerDTO createdOwner = ownerEntityMapper.map(
            ownerService.createOwner(
                ownerEntityMapper.map(new Owner(), ownerCreateDTO)
            )
        );
        URI location = URI.create("/owners/" + createdOwner.id());
        return ResponseEntity.created(location).body(createdOwner);
    }

    /**
     * Read single Owner
     */
    @GetMapping(value = "/{ownerId}")
    public ResponseEntity<OwnerDTO> findOwner(@PathVariable("ownerId") @Min(1) int ownerId) {
        return ResponseEntity.ok(ownerEntityMapper.map(ownerService.findOwner(ownerId)));
    }

    /**
     * Read List of Owners
     */
    @GetMapping
    public ResponseEntity<List<OwnerDTO>> findAll() {
        return ResponseEntity.ok(ownerService.getAllOwners().stream().map(ownerEntityMapper::map).toList());
    }

    /**
     * Update Owner
     */
    @PutMapping(value = "/{ownerId}")
    public ResponseEntity<OwnerDTO> updateOwner(@PathVariable("ownerId") @Min(1) int ownerId, @Valid @RequestBody OwnerUpdateDTO ownerUpdateDTO) {
        return ResponseEntity.ok(ownerEntityMapper.map(
            ownerService.updateOwner(
                ownerId,
                ownerEntityMapper.map(new Owner(), ownerUpdateDTO)
            )
        ));
    }
}
