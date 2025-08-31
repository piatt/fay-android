plugins {
    alias(libs.plugins.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":appointments:data:api"))
    
    testImplementation(libs.junit5)
    testImplementation(libs.kotlin.test)
}
tasks.withType<Test> {
    useJUnitPlatform()
}