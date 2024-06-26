import java.util.Properties

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

    versionCode = 2
    versionName = "2.1.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    viewBinding = true
    buildConfig = true
  }

  val localProperties = Properties()
  val localPropertiesFile = rootProject.file("local.properties")
  if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { inputStream ->
      localProperties.load(inputStream)
    }
  }

  val apiKey: String = localProperties.getProperty("azure_openai_api_key", "")
  val endpoint: String = localProperties.getProperty("azure_openai_endpoint", "")
  val visionEndpoint: String = localProperties.getProperty("azure_computer_vision_endpoint", "")
  val visionApiKey: String = localProperties.getProperty("azure_computer_vision_api_key", "")


  buildTypes {
    getByName("debug") {
      buildConfigField("String", "AZURE_OPENAI_API_KEY", "\"$apiKey\"")
      buildConfigField("String", "AZURE_OPENAI_ENDPOINT", "\"$endpoint\"")
      buildConfigField("String", "AZURE_COMPUTER_VISION_API_KEY", "\"$visionApiKey\"")
      buildConfigField("String", "AZURE_COMPUTER_VISION_ENDPOINT", "\"$visionEndpoint\"")
    }

    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

      buildConfigField("String", "AZURE_OPENAI_API_KEY", "\"$apiKey\"")
      buildConfigField("String", "AZURE_OPENAI_ENDPOINT", "\"$endpoint\"")
      buildConfigField("String", "AZURE_COMPUTER_VISION_API_KEY", "\"$visionApiKey\"")
      buildConfigField("String", "AZURE_COMPUTER_VISION_ENDPOINT", "\"$visionEndpoint\"")
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

  // Square libs
  implementation("com.squareup.logcat:logcat:0.1")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
  implementation("com.squareup.okhttp3:okhttp:4.9.1")

  // AndroidX lifecycle
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}