plugins {
    autowire(libs.plugins.kotlin.jvm)
}

group = property.project.group
version = property.project.version

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
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
