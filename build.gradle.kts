plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.gradleupShadow)
    alias(libs.plugins.runPaperTask)
}

group = "cc.dstm"
version = "1.0"

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    compileOnly(libs.paper.api)

    compileOnly(libs.kotlin.stdlib)
}

tasks {
    runServer {
        minecraftVersion(libs.versions.paper.server.get())
    }

    // output jar with shaded libs
    shadowJar {
        archiveClassifier = ""
        manifest { attributes["paperweight-mappings-namespace"] = "mojang" }
    }
    jar { enabled = false }
    build {dependsOn("shadowJar") }

    // process replacements
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}