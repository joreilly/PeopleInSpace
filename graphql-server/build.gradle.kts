plugins {
  id("kotlin-platform-jvm")
  id("org.jetbrains.kotlin.plugin.spring") version("1.6.10")
  id("org.jetbrains.kotlin.plugin.serialization")
  id("org.springframework.boot") version("2.5.6")
  id("com.google.cloud.tools.appengine") version("2.4.2")
  id("com.github.johnrengelman.shadow")
}


dependencies {
  implementation("com.expediagroup:graphql-kotlin-spring-server:5.3.0")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

  testImplementation("com.squareup.okhttp3:okhttp:4.9.3")

  with(Deps.Log) {
    implementation(logback)
  }

  implementation(project(":common"))
}

kotlin {
  sourceSets.all {
    languageSettings {
      optIn("kotlin.RequiresOptIn")
    }
  }
}

appengine {
  stage {
    setArtifact(tasks.named("bootJar").flatMap { (it as Jar).archiveFile })
  }
  deploy {
    projectId = "peopleinspace-graphql"
    version = "GCLOUD_CONFIG"
  }
}