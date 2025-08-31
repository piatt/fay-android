plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
}
android {
    namespace = "com.example.fay.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
        }
    }
}
dependencies {
    implementation(project(":auth:data:api"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.retrofit.android)
    implementation(libs.retrofit.converter)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlin.serialization)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}