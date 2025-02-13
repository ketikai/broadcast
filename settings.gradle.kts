rootProject.name = "broadcast"

pluginManagement {
    repositories {
        mavenLocal()
        maven {
            name = "aliyun"
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version "2.1.10"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
    id("com.highcapable.sweetdependency") version "1.0.4"
    id("com.highcapable.sweetproperty") version "1.0.5"
}

sweetProperty {
    global {
        buildScript {
            isEnable = true
        }
        sourcesCode {
            isEnable = false
        }
    }
    project(":protocol") {
        sourcesCode {
            isEnable = true
        }
    }
}

include(
    ":protocol",
    ":bungee",
    ":spigot",
)
