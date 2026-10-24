plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.3.10"
}  


android {
    namespace = "com.plat.osmium"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.plat.osmium"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
//    implementation(libs.androidx.room3.runtime)
//    ksp(libs.androidx.room3.compiler)
//
//    // ViewModel
//    implementation(libs.androidx.lifecycle.viewmodel.ktx)
//    // ViewModel utilities for Compose
//    implementation(libs.androidx.lifecycle.viewmodel.compose)
//    // LiveData
//    implementation(libs.androidx.lifecycle.livedata.ktx)
//    // Lifecycles only (without ViewModel or LiveData)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    // Lifecycle utilities for Compose
//    implementation(libs.androidx.lifecycle.runtime.compose)
//
//    // Saved state module for ViewModel
//    implementation(libs.androidx.lifecycle.viewmodel.savedstate)



    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    // implementation(libs.androidx.datastore)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.preference)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}