// Project-level build.gradle.kts

buildscript {
    val kotlin_version by extra("1.8.0") // Adjust Kotlin version if necessary

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2") // Adjust Gradle plugin version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
