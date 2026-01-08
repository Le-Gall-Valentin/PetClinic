package org.springframework.samples.petclinic.customers.model;

public interface Mapper<R, E> {
    E map(E response, R request);
}
