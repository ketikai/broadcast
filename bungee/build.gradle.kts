plugins {
    autowire(libs.plugins.kotlin.jvm)
    autowire(libs.plugins.gradleup.shadow)
}

group = property.project.group
version = property.project.version

dependencies {
    implementation(project(":protocol"))
    implementation(com.h2database.h2)
    implementation(com.zaxxer.hikariCP)
    implementation(org.quartz.scheduler.quartz)
    compileOnly(net.md.s.bungeecord.api)
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
            "example_name" to "\${example_name}",
        )
        inputs.properties(props)
        filteringCharset = property.project.encoding
        filesMatching(listOf("bungee.yml", "bundled/README.md")) {
            expand(props)
        }
    }
}
