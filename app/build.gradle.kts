plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.androidHilt)
    alias(libs.plugins.googleServices)
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.mordisko"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mordisko"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        val googleClientId: String = checkNotNull(
            project.findProperty("GOOGLE_WEB_CLIENT_ID") as? String
        ) {
            "GOOGLE_WEB_CLIENT_ID no está definido en gradle.properties"
        }
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleClientId\"")

    }

    signingConfigs {
        create("release") {
            storeFile = file("C:/Users/javie/upload-keystore.jks")
            storePassword = System.getenv("UPLOAD_STORE_PASSWORD")
            keyAlias = "upload"
            keyPassword = System.getenv("UPLOAD_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            // ASOCIA la firma de release
            signingConfig = signingConfigs.getByName("release")

            // Recomendado para producción
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
        }
    }

}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.08.00"))

    // ViewModel y Navigation
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Firebase BoM (¡usa solo una vez!)
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    // Firebase - sin versiones específicas
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-messaging")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Permisos
    implementation("com.google.accompanist:accompanist-permissions:0.33.2-alpha")

    // Corrutinas con Google Play Services
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.google.firebase.crashlytics.buildtools)
    kapt(libs.hilt.compiler)

    // Icons y material
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    implementation("com.google.android.material:material:1.6.0")

    // Coil para cargar imágenes desde URL
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Google Auth (Login con Google)
    implementation(libs.firebase.auth)
    implementation(libs.google.auth)

    // Compose + Material 3
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Mapa para calcular el Delivery
    implementation ("com.google.maps.android:android-maps-utils:3.4.0")
}
