<a href="http://choco.wtf/javadocs/locksecurity" alt="Javadocs">
    <img src="https://img.shields.io/badge/Javadocs-Regularly_updated-brightgreen" alt="Javadocs"/>
</a>
<a href="https://github.com/2008Choco/LockSecurity/wiki/" alt="Wiki">
    <img src="https://img.shields.io/static/v1?label=Plugin%20Wiki&message=Hosted%20by%20GitHub&color=3B3B3B&logo=github" alt="Wiki"/>
</a>
<a href="https://twitter.com/intent/follow?screen_name=2008Choco_" alt="Follow on Twitter">
    <img src="https://img.shields.io/twitter/follow/2008Choco_?style=social&logo=twitter" alt="Follow on Twitter">
</a>

# LockSecurity

For information about the plugin and how to use it, please see the plugin's [resource page on SpigotMC](https://www.spigotmc.org/resources/81282/) or the [official wiki here on GitHub](https://github.com/2008Choco/LockSecurity/wiki/).

# Depending on the LockSecurity API
LockSecurity deploys an API artifact to Jitpack for developers to depend on in order to interact with LockSecurity without having to purchase the project on SpigotMC. This artifact is provided free of charge. In order to use the API in your project, you must include the following in your project's build file:

(The latest version of the API may be found on the [GitHub Releases page](https://github.com/2008Choco/LockSecurity/releases/) for this repository).

## Maven
```xml
<project>
  ...
  <repositories>
    <id>jitpack</id>
    <url>https://jitpack.io/</url>
  </repositories>
  ...
  <dependencies>
    <dependency>
      <groupId>com.github.2008Choco</groupId>
      <artifactId>LockSecurity</artifactId>
      <version>API-VERSION-HERE</version>
      <scope>provided</scope>
    </dependency>
  </dependencies>
  ...
</project>
```

## Gradle
```groovy
  repositories {
    jcenter()
    maven { url "https://jitpack.io" }
  }

  dependencies {
    implementation 'com.github.2008Choco:LockSecurity:API-VERSION-HERE'
  }
```