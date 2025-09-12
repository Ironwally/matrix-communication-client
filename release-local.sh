#!/usr/bin/env bash
set -e
# Run tests and publish local to ./local-repo
./mvnw clean deploy -DaltDeploymentRepository=local::default::file:./local-repo \
  && echo "Published to $(cd ./local-repo && pwd)"