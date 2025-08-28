import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
}
android {
    namespace = "com.example.fay"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.fay"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget("11")
        }
    }
    buildFeatures {
        compose = true
    }
}
dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    implementation(project(":auth:ui"))
    implementation(project(":appointments:ui"))
    implementation(project(":chat:ui"))
    implementation(project(":journal:ui"))
    implementation(project(":profile:ui"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    testImplementation(libs.hilt.testing)
    androidTestImplementation(libs.hilt.testing)
    kapt(libs.hilt.compiler)
    kaptTest(libs.hilt.compiler)
    kaptAndroidTest(libs.hilt.compiler)

    implementation(libs.retrofit.android)
    implementation(libs.retrofit.converter)
    implementation(libs.kotlin.serialization)

    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mock.web.server)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.kotlin.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}