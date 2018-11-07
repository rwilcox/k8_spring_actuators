package com.wilcoxd.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.health.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 *
 * Liveliness asks if the container is alive . Failure here
 * means that the K8 pod is killed (it must have zombied or something).
 *
 * Spring already has a way to know we have a healthy app, the health check.
 * If that has failed something has gone very wrong.
 *
 * Yes, we could use the health check directly, but that leaks information that k8
 * doesn't care about.
 *
 */
@Component
@Endpoint(id="liveliness")
public class LivelinessActuator {
    @Autowired
    HealthIndicatorRegistry registry;

    @Autowired
    List<HealthIndicator> healthIndicators;

    @ReadOperation
    public WebEndpointResponse<Map> invoke() {

        // mostly based on: http://micrometer.io/docs/guide/healthAsGauge
        // ... but Spring Boot 2.0 changed some stuff around, so we will too

        Map<String, HealthIndicator> indicators = registry.getAll();
        boolean allAreUp = indicators.entrySet().stream().allMatch(/* Map<...>.Entry */ healthIndicator -> {
                Health currentHealth = healthIndicator.getValue().health();
                boolean result = currentHealth.getStatus().getCode().equals("UP");

                if (result == false) {
                    System.out.println(
                            String.format("Health Indicator Failed! Message: %s", currentHealth.toString() )
                    );
                }
                return result;
            }
        );

        WebEndpointResponse output;
        if (allAreUp) {
            output = new WebEndpointResponse("{\"status\": \"OK\"}", WebEndpointResponse.STATUS_OK);
        } else {
            output = new WebEndpointResponse("{\"status\": \"DOWN\"}", WebEndpointResponse.STATUS_SERVICE_UNAVAILABLE);
        }

        return output;
    }
}
