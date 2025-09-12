#!/usr/bin/env pwsh
$ErrorActionPreference = 'Stop'
# Run tests and publish local to ./local-repo
./mvnw.cmd clean deploy -DaltDeploymentRepository=local::default::file:./local-repo;
Write-Host "Published to $(Resolve-Path ./local-repo)" -ForegroundColor Green