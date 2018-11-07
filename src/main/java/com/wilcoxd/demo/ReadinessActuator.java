package com.wilcoxd.demo;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *
 * Readiness asks if the container is ready for requests.
 * Failure here means k8 will not send requests to the pod (and try again later
 * to see if we are OK).
 *
 * Why might a pod be unready? Practically in Java web services there's two potential answers:
 *   a) app is starting
 *   b) you have exhausted your Tomcat thread pool. Are you getting DDOSSed or doing something
 *      dumb like mining a Bitcoin block during an HTTP request?
 *   c) GC churnning
 *
 * The former case can be taken care of by k8 backing off. The latter case will be handled by the pod
 * just not responding to its health check. Option B is far more interesting
 *
 * So look at the current number of used threads in our Tomcat pool and see if we need to tell K8 to
 * back off (and/or scale up).
 *
 * You could also look up something that would turn off traffic temporarily. Like say an mbean?
 */
@Component
@Endpoint(id = "readiness")
public class ReadinessActuator {

    @Autowired
    private MeterRegistry repo;

    @ReadOperation
    public WebEndpointResponse<Map> invoke() {
        Gauge busyThreads = repo.get("tomcat.threads.busy").gauge();
        Gauge allThreads  = repo.get("tomcat.threads.config.max").gauge();  // yes, could do @Value("${server.tomcat.max-threads:200}") and have it injected

        double busyThreadsCount = busyThreads.value();
        double allThreadsCount  = allThreads.value();

        double maxThreadUsage = allThreadsCount * 0.7; // at 70% full something's up, give k8 time to scale up (we hope...)

        WebEndpointResponse output;
        if ( busyThreadsCount > maxThreadUsage ) {
            output = new WebEndpointResponse("{\"status\": \"HELP\"}", WebEndpointResponse.STATUS_TOO_MANY_REQUESTS );
            System.out.println(
                    String.format("setting ready to FALSE, current threads %f out of %f/%f", busyThreadsCount, maxThreadUsage, allThreadsCount)
            );
        }
        else {
            output = new WebEndpointResponse<>("{\"status\": \"OK\"}", WebEndpointResponse.STATUS_OK);
        }

        return output;
    }

}
