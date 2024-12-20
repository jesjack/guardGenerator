plugins {
    kotlin("jvm") version "1.9.22" // Updated Kotlin version
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.jesjack"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven { url = uri("https://maven.enginehub.org/repo/") } // Updated WorldGuard repository
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT") // Updated Paper API version
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9") // Updated WorldGuard version
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}