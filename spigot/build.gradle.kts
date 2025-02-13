plugins {
    autowire(libs.plugins.kotlin.jvm)
    autowire(libs.plugins.gradleup.shadow)
}

group = property.project.group
version = property.project.version

dependencies {
    implementation(project(":protocol"))
    compileOnly(org.spigotmc.spigot.api)
    compileOnly(me.clip.placeholderapi)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(property.project.javaVersion))
        vendor.set(JvmVendorSpec.AZUL)
    }
    jvmToolchain(8)
}

tasks.apply {
    withType<JavaCompile> {
        options.encoding = property.project.encoding
    }

    processResources {
        val name = property.project.name
        val props = mutableMapOf(
            "name" to name,
            "version" to version,
            "main" to "${property.project.group}.${project.name}.${property.project.entryName}",
            "command" to property.project.command.main,
            "aliasCommand" to property.project.command.alias,
        )
        inputs.properties(props)
        filteringCharset = property.project.encoding
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
