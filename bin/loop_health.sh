#!/usr/bin/env bash


while [ 1 ]
do
    curl http://localhost:8081/actuator/readiness
    sleep 1

done

