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
package org.springframework.samples.petclinic.vets.controller.specialty;

import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vets.model.specialty.DTO.SpecialtyDTO;
import org.springframework.samples.petclinic.vets.model.specialty.SpecialtyMapper;
import org.springframework.samples.petclinic.vets.service.specialty.SpecialtyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/specialties")
@RestController
@Validated
public class SpecialtyController {
    private final SpecialtyService specialtyService;
    private final SpecialtyMapper specialtyMapper;

    SpecialtyController(SpecialtyService specialtyService, SpecialtyMapper specialtyMapper) {
        this.specialtyService = specialtyService;
        this.specialtyMapper = specialtyMapper;
    }


    @GetMapping
    public ResponseEntity<List<SpecialtyDTO>> getAllSpecialties() {
        return ResponseEntity.ok(specialtyService.getAllSpecialties().stream().map(specialtyMapper::map).toList());
    }
}
