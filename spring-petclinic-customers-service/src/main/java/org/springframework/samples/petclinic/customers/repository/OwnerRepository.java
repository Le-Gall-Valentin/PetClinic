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
package org.springframework.samples.petclinic.customers.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.customers.model.owner.Owner;

/**
 * Repository class for <code>Owner</code> domain objects All method names are compliant with Spring Data naming
 * conventions so this interface can easily be extended for Spring Data See here: http://static.springsource.org/spring-data/jpa/docs/current/reference/html/jpa.repositories.html#jpa.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Maciej Szarlinski
 */
public interface OwnerRepository extends JpaRepository<Owner, Integer>, JpaSpecificationExecutor<Owner> {

    @Query("""
        SELECT o
        FROM Owner o
        WHERE (:firstName IS NULL OR :firstName = '' OR LOWER(o.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
          AND (:lastName IS NULL OR :lastName = '' OR LOWER(o.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
          AND (:city IS NULL OR :city = '' OR LOWER(o.city) LIKE LOWER(CONCAT('%', :city, '%')))
          AND (
              :petName IS NULL OR :petName = ''
              OR EXISTS (
                  SELECT 1
                  FROM Pet p
                  WHERE p.owner = o
                    AND LOWER(p.name) LIKE LOWER(CONCAT('%', :petName, '%'))
              )
          )
        """)
    Page<Owner> searchOwners(
        @Param("petName") String petName,
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("city") String city,
        Pageable pageable
    );
}
