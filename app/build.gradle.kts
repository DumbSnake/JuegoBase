plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services") // Para Firebase
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"

}

android {
    namespace = "com.example.prueba"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.prueba"
        minSdk = 26
        targetSdk = 34 // Mantener temporalmente en 34 para evitar problemas
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15" // Última versión estable
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.6.0")) // Última versión del BOM
    implementation("com.google.firebase:firebase-auth-ktx") {
        exclude(group = "com.sun.activation", module = "javax.activation")
    }
    implementation("com.google.firebase:firebase-analytics-ktx") {
        exclude(group = "com.sun.activation", module = "javax.activation")
    }

    // Core AndroidX
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Compose
    implementation("androidx.compose.ui:ui:1.7.5")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.foundation:foundation:1.7.5")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.5")
    implementation("androidx.activity:activity-compose:1.9.3")

    // Lifecycle y LiveData
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.5")

    // Navegación
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("com.google.dagger:hilt-android:2.49")
    implementation(libs.firebase.firestore.ktx)

    // Pruebas
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.5")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.5")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.5")

    // Material Icons
    implementation("androidx.compose.material:material-icons-extended:1.7.5")

    //google servicio
    implementation("com.google.android.gms:play-services-auth:21.2.0")
}

configurations.all {
    // Excluir javax.activation si se incluye accidentalmente
    exclude(group = "com.sun.activation", module = "javax.activation")
}

