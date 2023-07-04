@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = Setup.App.namespace
    compileSdk = Setup.compileSdk

    defaultConfig {
        applicationId = Setup.App.applicationId
        minSdk = Setup.minSdk
        targetSdk = Setup.targetSdk

        versionCode = Setup.versionCode
        versionName = Setup.versionName

        resValue("string", "app_name", Setup.App.name)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android.txt"))
            proguardFile("proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=enable",
        )
    }
}

dependencies {
    implementation(project(":arclayout"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.flaviofaria:kenburnsview:1.0.7")
}
