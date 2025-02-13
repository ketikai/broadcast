import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    autowire(libs.plugins.kotlin.jvm) apply false
    autowire(libs.plugins.gradleup.shadow) apply false
}

subprojects {
    tasks.whenTaskAdded {
        this is ShadowJar || return@whenTaskAdded
        name == "shadowJar" || return@whenTaskAdded
        doFirst {
            this as ShadowJar
            archiveBaseName.set(rootProject.name)
            archiveClassifier.set(project.name)
        }
    }
}
