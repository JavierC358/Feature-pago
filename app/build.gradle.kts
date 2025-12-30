plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)

    id("org.jetbrains.kotlin.kapt")

    alias(libs.plugins.androidHilt)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
}

android {
    namespace = "com.example.mordisko"
    compileSdk = 35

    defaultConfig {

        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${project.findProperty("GOOGLE_WEB_CLIENT_ID")}\"")

        applicationId = "com.sazon.app"
        minSdk = 24
        targetSdk = 35

        versionCode = 7
        versionName = "1.0.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("C:/Users/javie/upload-keystore.jks")
            storePassword = "sazon0608"
            keyAlias = "upload"
            keyPassword = "sazon0608"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true

        buildConfig = true

    }

    // Kotlin 1.9.24 -> Compose compiler 1.5.14 OK
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // -------------------------
    // Compose BOM (una sola vez)
    // -------------------------
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // -------------
    // Compose UI
    // -------------
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    // ----------------
    // AndroidX núcleo
    // ----------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    // -------------------------
    // Navigation + Hilt-Nav
    // -------------------------
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // -------------------------
    // Firebase BOM (una sola vez)
    // -------------------------
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics.ktx)

    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.storage.ktx)

    // -------------------------
    // Google Auth (Sign-In)
    // -------------------------
    implementation(libs.google.auth)

    // -------------------------
    // Google Maps
    // -------------------------
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")

    // -------------------------
    // Retrofit
    // -------------------------
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // -------------------------
    // Permisos (Accompanist)
    // -------------------------
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // ------------------------------------
    // Corrutinas con Google Play Services
    // ------------------------------------
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // -------------------------
    // Hilt
    // -------------------------
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // -----------------------------------------
    // Material Components (solo si usas vistas)
    // -----------------------------------------
    implementation("com.google.android.material:material:1.12.0")

    // -------------------------
    // Coil
    // -------------------------
    implementation("io.coil-kt:coil-compose:2.5.0")

    // -------------------------
    // Tests
    // -------------------------
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.compose.material.icons.extended)
}

kapt {
    correctErrorTypes = true
}