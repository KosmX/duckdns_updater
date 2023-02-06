plugins {
    java
    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("org.jsoup:jsoup:${project.property("jsoup_version")}")
}

kotlin{
    jvmToolchain(java.targetCompatibility.majorVersion.toInt())
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release.set(java.targetCompatibility.majorVersion.toInt())
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = java.targetCompatibility.toString()
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName.get()}" }
        }
        manifest {
            attributes("Main-Class" to "dev.kosmx.duckdnsClient.MainKt")
        }
        archiveClassifier.set("slim")
    }

    shadowJar {
        archiveClassifier.set("")
    }
    build {
        dependsOn(shadowJar)
    }
}