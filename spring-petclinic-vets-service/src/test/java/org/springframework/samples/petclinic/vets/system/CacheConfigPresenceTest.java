package org.springframework.samples.petclinic.vets.system;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("production")
class CacheConfigPresenceTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void cachingBeansPresentInProduction() {
        int count = context.getBeanNamesForType(AnnotationCacheOperationSource.class).length;
        // At least one cache operation source when @EnableCaching is active
        org.junit.jupiter.api.Assertions.assertTrue(count > 0);
    }

    @Nested
    @SpringBootTest
    @ActiveProfiles("test")
    class CacheConfigAbsenceTest {
        @Autowired
        private ApplicationContext context;

        @Test
        void cachingBeansAbsentInTest() {
            int count = context.getBeanNamesForType(AnnotationCacheOperationSource.class).length;
            assertEquals(1, count);
        }
    }
}
