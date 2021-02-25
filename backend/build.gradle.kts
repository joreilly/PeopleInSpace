import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    id("com.google.protobuf")
    id("io.vertx.vertx-plugin")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("javax.annotation:javax.annotation-api:1.3.2") // compile only

    // KOTLINX
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerialization}") // JVM dependency

    // KTOR
    implementation("io.ktor:ktor-server-core:${Versions.ktor}")
    implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
    implementation("io.ktor:ktor-serialization:${Versions.ktor}")
    implementation("io.ktor:ktor-websockets:${Versions.ktor}")

    // VERTX
    implementation("io.vertx:vertx-core:${Versions.vertx}")
    implementation("io.vertx:vertx-lang-kotlin:${Versions.vertx}")
    implementation("io.vertx:vertx-grpc:${Versions.vertx}")

    // GRPC
    implementation("io.grpc:grpc-protobuf:${Versions.grpc}")
    implementation("io.grpc:grpc-all:${Versions.grpc}")
    implementation("io.grpc:protoc-gen-grpc-kotlin:1.0.0")
    api("io.grpc:grpc-kotlin-stub:1.0.0")

    // PROTOCOL BUFFERS
    api("com.google.protobuf:protobuf-java-util:3.13.0")
    implementation("com.google.protobuf:protobuf-gradle-plugin:0.8.13")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    implementation(project(":common"))
}

java {
    sourceSets.getByName("main").resources.srcDir("src/main/proto")
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
    sourceCompatibility = "15"
    targetCompatibility = "15"
    kotlinOptions {
        jvmTarget = "15"
        apiVersion = "1.4"
        languageVersion = "1.4"
//        allWarningsAsErrors = true
    }
}

sourceSets["main"].java {
    srcDir("build/generated/source/proto/main/java")
    srcDir("build/generated/source/proto/main/grpc")
    srcDir("build/generated/source/proto/main/grpckt")
}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Versions.protobuf}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${Versions.grpc}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.0.0:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

application {
    mainClass.set("BackendApplication")
}

//tasks.startScripts {
//    applicationName = "mercado-backend"
//}
//
//tasks.distZip {
//    archiveBaseName.set("mercado-backend")
//}
//
//val helloWorldServerStartScripts = tasks.register<CreateStartScripts>("greeterServerStartScripts") {
//    mainClassName = "HelloWorldServer"
//    applicationName = "greeter-server"
//    outputDir = tasks.named<CreateStartScripts>("startScripts").get().outputDir
//    classpath = tasks.named<CreateStartScripts>("startScripts").get().classpath
//}
//
//tasks.named("startScripts") {
//    dependsOn(helloWorldServerStartScripts)
//}

vertx {
    mainVerticle = "HelloWorldServer"  // (2)
}

//applicationDistribution.into('bin') {
//    from(helloWorldServerStartScripts)
//    fileMode = 0755
//}

// Heroku
tasks.register("stage") {
    dependsOn("build", "vertxRun")
}