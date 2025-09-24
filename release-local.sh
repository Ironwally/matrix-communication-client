#!/usr/bin/env bash
set -euo pipefail

# Build, run tests, attach sources/javadocs, and install to local Maven repo (~/.m2)
# Jar sources and javadocs are attached in this script to avoid duplicate attachments with the parent. 
./mvnw \
  clean \
  source:jar-no-fork \
  javadoc:jar \
  install \
  -Dgcf.skip=true

echo "Installed to local Maven repository (~/.m2/repository)"