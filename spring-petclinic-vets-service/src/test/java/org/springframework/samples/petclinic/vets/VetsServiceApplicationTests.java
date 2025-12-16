package org.springframework.samples.petclinic.vets;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class VetsServiceApplicationTests {

    @Test
    void contextLoads() {
        // no-op; verifies application context starts
    }
}
