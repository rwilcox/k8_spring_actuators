package com.wilcoxd.demo;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {

        return Health.up().build();
        // return Health.down().withDetail("customhealth", "I don't feel like it").build();
    }
}
