package org.springframework.samples.petclinic.vets.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResourceNotFoundExceptionTest {

    @Test
    void hasResponseStatusNotFound() {
        ResponseStatus status = ResourceNotFoundException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(status);
        assertEquals(HttpStatus.NOT_FOUND, status.value());
    }
}
