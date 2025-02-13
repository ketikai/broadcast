plugins {
    autowire(libs.plugins.kotlin.jvm)
}

group = property.project.group
version = property.project.version

dependencies {
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
}
