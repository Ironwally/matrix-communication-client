#!/usr/bin/env pwsh
$ErrorActionPreference = 'Stop'

# Build, run tests, attach sources/javadocs, and install to local Maven repo
./mvnw.cmd clean source:jar-no-fork javadoc:jar install -Dgcf.skip=true;
Write-Host "Installed to local Maven repository (~/.m2/repository)" -ForegroundColor Green