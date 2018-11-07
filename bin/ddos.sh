#!/usr/bin/env bash

set -e

# DDOS myself on the slow endpoint
# hit ctrl-c a bunch of times when you've had enough...

while [ 1 ]
do
    curl http://localhost:8080/ &
done