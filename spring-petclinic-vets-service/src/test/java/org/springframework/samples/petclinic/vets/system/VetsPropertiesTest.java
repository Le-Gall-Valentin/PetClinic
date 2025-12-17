package org.springframework.samples.petclinic.vets.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class VetsPropertiesTest {

    @Autowired
    private VetsProperties properties;

    @Test
    void bindsCachePropertiesFromYaml() {
        assertNotNull(properties);
        assertNotNull(properties.cache());
        assertEquals(10, properties.cache().ttl());
        assertEquals(10, properties.cache().heapSize());
    }
}
