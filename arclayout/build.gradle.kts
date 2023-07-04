@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = Setup.Lib.namespace
    compileSdk = Setup.compileSdk

    defaultConfig {
        minSdk = Setup.minSdk
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android.txt"))
            proguardFile("proguard-rules.pro")
        }
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
    implementation("androidx.appcompat:appcompat:1.6.1")
}

apply(from = "$projectDir/publish.gradle.kts")

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }

    artifacts {
        archives(sourcesJar)
    }
}

project.afterEvaluate {
    project.tasks["publishMavenJavaPublicationToMavenLocal"].dependsOn("bundleReleaseAar")
    project.tasks["publishMavenJavaPublicationToMavenRepository"].dependsOn("bundleReleaseAar")
    project.tasks["signMavenJavaPublication"].dependsOn("bundleReleaseAar")
}
