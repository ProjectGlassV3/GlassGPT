plugins {
  id("com.android.application")
  id("kotlin-android")
}

android {
  namespace = "com.roxxonglobal.glassgpt"

  defaultConfig {
    applicationId = "com.roxxonglobal.glassgpt"
    compileSdk = 34
    minSdk = 27
    targetSdk = 34

    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    viewBinding = true
    buildConfig = true
  }

  buildTypes {
    getByName("debug") {
      // TODO: please pull this from your local.properties file
      buildConfigField("String", "API_KEY", "\"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\"")
    }

    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

      // TODO: please pull this from your local.properties file
      buildConfigField(
        "String", "API_KEY", "\"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\""
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

dependencies {
  // Core AndroidX
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("androidx.core:core-ktx:1.12.0")

  // UI
  implementation("com.google.android.material:material:1.10.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")

  // ChatGPT library TODO replace with retrofit / direct communication
  implementation("com.theokanning.openai-gpt3-java:service:0.18.2")

  // Square libs
  implementation("com.squareup.logcat:logcat:0.1")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

  // AndroidX lifecycle
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}