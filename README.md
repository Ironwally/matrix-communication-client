[![Build Status](https://github.com/Cosium/matrix-communication-client/actions/workflows/ci.yml/badge.svg)](https://github.com/Cosium/matrix-communication-client/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.cosium.matrix_communication_client/matrix-communication-client.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.cosium.matrix_communication_client%22%20AND%20a%3A%22matrix-communication-client%22)


# Matrix Communication Client

A [Matrix](https://matrix.org/) java client.

# Example

```java
public class Example {

  public static void main(String[] args) {
    MatrixResources matrix =
        MatrixResources.factory()
            .builder()
            .https()
            .hostname("matrix.example.org")
            .defaultPort()
            .usernamePassword("jdoe", "secret")
            .build();

    RoomResource room = matrix
        .rooms()
        .create(
            CreateRoomInput.builder()
                .name("Science")
                .roomAliasName("science")
                .topic("Anything about science")
                .build());
	
    room.sendMessage(Message.builder().body("Hello !").formattedBody("<b>Hello !</b>").build());
  }
}
```

Another example with existing room:

```java
public class Example {

  public static void main(String[] args) {
    MatrixResources matrix =
        MatrixResources.factory()
            .builder()
            .https()
            .hostname("matrix.example.org")
            .defaultPort()
            .usernamePassword("jdoe", "secret")
            .build();

    RoomResource room = matrix
        .rooms()
        .byId("!PVvauSmjcHLwoAJkyT:matrix.example.org");
	
    room.sendMessage(Message.builder().body("Hello !").formattedBody("<b>Hello !</b>").build());
  }
}
```

# Dependency

Local only currently. 
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

2. Build (runs tests) and deploy to a local file repository:

  Windows PowerShell (script):
  ```powershell
  ./release-local.ps1
  ```
  macOS / Linux:
  ```bash
  ./release-local.sh
  ```

<a id="local-publish-3"></a>
3. Add this repository to another project's `pom.xml` to consume the artifact:
   ```xml
   <repositories>
     <repository>
       <id>local-repo</id>
       <url>file:///${project.basedir}/relative/path/to/this/project/local-repo</url>
       <snapshots><enabled>true</enabled></snapshots>
       <releases><enabled>true</enabled></releases>
     </repository>
   </repositories>
   <dependency>
     <groupId>io.github.ironwally</groupId>
     <artifactId>matrix-communication-client-fork</artifactId>
     <version>1.10-SNAPSHOT</version>
   </dependency>
   ```

