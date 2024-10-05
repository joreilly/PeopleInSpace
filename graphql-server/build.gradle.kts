
plugins {
  kotlin("multiplatform")
  id("org.jetbrains.kotlin.plugin.spring") version("1.9.25")
  kotlin("plugin.serialization")
  id("org.springframework.boot") version("3.1.5")
  //id("com.google.cloud.tools.appengine") version("2.4.2")
  id("com.github.johnrengelman.shadow")
}


kotlin {
  jvm() {
    withJava()
  }

  sourceSets {
    val jvmMain by getting {
      dependencies {
        implementation("com.expediagroup:graphql-kotlin-spring-server:5.5.0")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

        implementation("ch.qos.logback:logback-classic:1.5.8")

        implementation(projects.common)
      }
    }
  }
}


//appengine {
//  stage {
//    setArtifact(tasks.named("bootJar").flatMap { (it as Jar).archiveFile })
//  }
//  deploy {
//    projectId = "peopleinspace-graphql"
//    version = "GCLOUD_CONFIG"
//  }
//}