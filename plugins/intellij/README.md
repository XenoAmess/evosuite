Build the IntelliJ plugin with JDK 21:

```sh
cd plugins/intellij
./gradlew buildPlugin
```

For a local HTTP proxy, configure `~/.gradle/gradle.properties`:

```properties
systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=10808
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=10808
```

Keep proxy settings in the user Gradle properties file so each machine can use
its own network configuration. The Wrapper allows a 120-second network timeout,
retries failed downloads three times, and verifies the distribution SHA-256.
