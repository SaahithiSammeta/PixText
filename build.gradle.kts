// Project-level build.gradle.kts

buildscript {
    val kotlin_version by extra("1.8.0")

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }

}

plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false // Google services plugin
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
