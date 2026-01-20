package org.springframework.samples.petclinic.visits.service.pet;
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


/**
 * @author Maciej Szarlinski
 */
public record PetDetails(
    int id,
    String name,
    String owner,
    String birthDate,
    PetType type,
    boolean deleted) {

    public static final class PetDetailsBuilder {
        private int id;
        private String name;
        private String birthDate;
        private PetType type;
        private String owner;
        private boolean deleted;

        private PetDetailsBuilder() {
        }

        public static PetDetailsBuilder aPetDetails() {
            return new PetDetailsBuilder();
        }

        public PetDetailsBuilder id(int id) {
            this.id = id;
            return this;
        }

        public PetDetailsBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PetDetailsBuilder birthDate(String birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public PetDetailsBuilder type(PetType type) {
            this.type = type;
            return this;
        }

        public PetDetailsBuilder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public PetDetailsBuilder deleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public PetDetails build() {
            return new PetDetails(id, name, owner, birthDate, type, deleted);
        }
    }
}
