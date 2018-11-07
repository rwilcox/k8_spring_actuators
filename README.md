# Spring Boot and K8 health endpoints example

The Spring Boot project includes [Spring Cloud Kubernetes](https://github.com/spring-cloud/spring-cloud-kubernetes) which wraps K8's secrets, config and service discovery.

However, it doesn't add much for [k8 liveliness and readiness probes](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-probes/).


## K8 Specific Actuators

This exposes two additional actuators, `LivelinessActuator` and `ReadinessActuator`.

### Liveliness Actuator

This returns an error code if the Spring health endpoints fail, else returns ok.

### Readiness Actuator

This checks [Tomcat metrics](https://github.com/rwilcox/my-learnings-docs/blob/master/learning_ops_java_spring.md#learning_ops_java_tomcat_spring_boot_metrics) and will fail if the number of currently busy threads is 70% of the Tomcat threadpool.

# Utilities

Also in this package:

  * SlowController: `http://localhost:8080/` <-- takes 1 second to return
  * `CustomHealthActuator` <-- want to see your liveliness endpoint fail? Make this health check endpoint fail.
  * `bin/` <-- contains scripts to DDoS your machine!

