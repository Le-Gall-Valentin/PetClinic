package org.springframework.samples.petclinic.visits.config;

import io.micrometer.core.aop.TimedAspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MetricConfigPresenceTest {
    @Autowired
    private TimedAspect timedAspect;

    @Test
    void timedAspectBeanIsPresent() {
        assertThat(timedAspect).isNotNull();
    }
}

