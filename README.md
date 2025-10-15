[![Build Status](https://github.com/Cosium/matrix-communication-client/actions/workflows/ci.yml/badge.svg)](https://github.com/Cosium/matrix-communication-client/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.cosium.matrix_communication_client/matrix-communication-client.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.cosium.matrix_communication_client%22%20AND%20a%3A%22matrix-communication-client%22)


# Matrix Communication Client

A [Matrix](https://matrix.org/) java client.

# Usage
You can find usage examples in src/test

# Documentation
- Project Specification:
- Matrix Specification and API: https://spec.matrix.org/v1.16/client-server-api/
  - Client Authentication (Legacy) API: https://spec.matrix.org/v1.16/client-server-api/
  - Messages API: https://spec.matrix.org/v1.16/client-server-api/#instant-messaging
- Synapse Homeserver Specification and API: https://element-hq.github.io/synapse/latest/welcome_and_overview.html

# Developement

## Current Problems
- Too open classes. Needs complete restructure to fix longterm

## Planned project restructure
The project has become very closely coupled. Modules are not finely separated and MatrixApi acts as a central "god" class for api related operations

**Goals**
- Decompose project into several small modules
- Clear separation of modules with DTOs for intermodule communication
- Separate Objects and Json representation (via DTOs?)
- Remove duplicate interface implementations

## Further additions
- Room delete
- Room join
- Room add user

- User:
- User Register: https://spec.matrix.org/v1.16/client-server-api/#post_matrixclientv3register
  - oder via commandline: https://element-hq.github.io/synapse/latest/setup/installation.html?highlight=user#registering-a-user
- User change Password: https://spec.matrix.org/v1.16/client-server-api/#post_matrixclientv3accountpassword
  - or via synapse admin api: https://element-hq.github.io/synapse/latest/admin_api/user_admin_api.html?highlight=password#reset-password
- User deactivate: https://spec.matrix.org/v1.16/client-server-api/#post_matrixclientv3accountdeactivate
-

# Dependency

Local maven dependency only currently.
See [Local publish 3.](#local-publish-3) on how to include.

# Publishing
## Local publish
1. Version:
- ongoing developement: keep `x.y.z-SNAPSHOT`
- For release version: remove `-SNAPSHOT` -> `x.y.z`
- After release: bump to next snapshot: add `-SNAPSHOT`
  - Version bumping Scheme:
    - (Patch release) x.y.*z+*: backward-compatible fixes: bugs/security/performance/reliability/stability/misleading API behavior/doc corrections that prevent user errors
    - (Minor release) x.*y+*.z: backward-compatible features or deprecations
    - (Major release) *x+*.y.z: API breaking/behavior breaking changes

2. Build (runs tests) and install to your local Maven repository (~/.m2):

  Windows PowerShell (script):
  ```powershell
  ./release-local.ps1
  ```
  macOS / Linux:
  ```bash
  ./release-local.sh
  ```

<a id="local-publish-3"></a>
3. Consume from your local Maven repository:
   - Maven:
     ```xml
     <dependency>
       <groupId>io.github.ironwally</groupId>
       <artifactId>matrix-communication-client-fork</artifactId>
       <version>1.10-SNAPSHOT</version>
     </dependency>
     ```

   - Gradle (Groovy DSL):

   ```groovy
   // build.gradle
   repositories {
     mavenLocal()
   }

   dependencies {
     implementation("io.github.ironwally:matrix-communication-client-fork:1.10-SNAPSHOT")
   }
   ```

   - Gradle (Kotlin DSL):

   ```kotlin
   // build.gradle.kts
   repositories {
     mavenLocal()
   }

   dependencies {
     implementation("io.github.ironwally:matrix-communication-client-fork:1.10-SNAPSHOT")
   }
   ```


