

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false // Versión más reciente
    alias(libs.plugins.compose.compiler) apply false

}


buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.1.1") // Versión compatible con compileSdk 35
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10") // Última versión de Kotlin
        classpath ("com.google.gms:google-services:4.4.2") // Plugin para Firebase
    }
}


