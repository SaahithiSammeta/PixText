plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize") // Add this plugin for @Parcelize support
}

android {
    namespace = "uk.ac.tees.mad.S3269326"
    compileSdk = 34

    defaultConfig {
        applicationId = "uk.ac.tees.mad.S3269326"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Set the Java version to 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Configure Kotlin JVM target to 17
    kotlinOptions {
        jvmTarget = "17"
    }

    viewBinding {
        enable = true
    }

    packagingOptions {
        // Ignore duplicate META-INF files to avoid conflicts
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
    }
}

dependencies {
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.storage.ktx)
    val kotlin_version = "1.8.0" // Ensure this matches in both files

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.1.1") // Firebase Auth SDK
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.amazonaws:aws-android-sdk-s3:2.16.12")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

}
