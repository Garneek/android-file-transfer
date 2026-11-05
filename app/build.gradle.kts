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
  signingConfigs {
    create("release") {
      keyAlias = "key"
      keyPassword = "111111"
      storeFile = file("../key.jks")
      storePassword = "111111"
    }
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
      signingConfig = signingConfigs.getByName("release")
      isMinifyEnabled = false
      isShrinkResources = false
//      isMinifyEnabled = true
//      isShrinkResources = true
      proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))
      optimization {
        enable = true
      }
    }
    debug {
      optimization {
        enable = false
      }
      isMinifyEnabled = false
      isShrinkResources = false
      isCrunchPngs = false
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
  implementation("io.coil-kt.coil3:coil-compose:3.6.3")
  implementation("io.coil-kt.coil3:coil-video:3.6.3")
  implementation(libs.androidx.compose.material3.adaptive.navigation.suite)

  implementation(libs.androidx.material3)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room3.common)
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")

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