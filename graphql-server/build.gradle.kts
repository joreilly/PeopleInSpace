
plugins {
  kotlin("multiplatform")
  id("org.jetbrains.kotlin.plugin.spring") version("1.8.20")
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
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

        implementation("ch.qos.logback:logback-classic:1.2.3")

        implementation(project(":common"))
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