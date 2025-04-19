import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)

    id ("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

}

android {
    namespace = "com.example.thriftr"
    compileSdk = 34

    val properties = Properties()
    properties.load(rootProject.file("local.properties").inputStream())

    defaultConfig {
        applicationId = "com.example.thriftr"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "APPWRITE_PROJECT_ID", "\"${properties["APPWRITE_PROJECT_ID"]}\"")
        buildConfigField("String", "APPWRITE_DATABASE_ID", "\"${properties["APPWRITE_DATABASE_ID"]}\"")
        buildConfigField("String", "APPWRITE_USER_COLLECTION_ID", "\"${properties["APPWRITE_USER_COLLECTION_ID"]}\"")
        buildConfigField("String", "APPWRITE_PRODUCT_COLLECTION_ID", "\"${properties["APPWRITE_PRODUCT_COLLECTION_ID"]}\"")
        buildConfigField("String", "APPWRITE_CART_COLLECTION_ID", "\"${properties["APPWRITE_CART_COLLECTION_ID"]}\"")
        buildConfigField("String", "APPWRITE_WISHLIST_COLLECTION_ID", "\"${properties["APPWRITE_WISHLIST_COLLECTION_ID"]}\"")
        buildConfigField("String", "APPWRITE_ORDER_COLLECTION_ID", "\"${properties["APPWRITE_ORDER_COLLECTION_ID"]}\"")
        buildConfigField("String", "APPWRITE_PRODUCT_IMAGE_BUCKET_ID", "\"${properties["APPWRITE_PRODUCT_IMAGE_BUCKET_ID"]}\"")
        buildConfigField("String", "APPWRITE_PROFILE_IMAGE_BUCKET_ID", "\"${properties["APPWRITE_PROFILE_IMAGE_BUCKET_ID"]}\"")

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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //AppWrite
    implementation("io.appwrite:sdk-for-android:5.1.1")

    //Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //Hilt for Jetpack Compose with navigation
    implementation(libs.androidx.hilt.navigation.compose)

    //Jetpack compose navigation
    implementation (libs.androidx.navigation.compose)

    //Material Icons Extended
    implementation (libs.androidx.material.icons.extended)

    //Splashscreen API
    implementation(libs.androidx.core.splashscreen)

   //Navigation Animation
  //  implementation ("androidx.compose.animation:animation:1.6.8")

    //Colorpicker library
    implementation (libs.colorpicker.compose)

    //Coil
    implementation(libs.coil.compose)

    //Carousel -Accompanist Library
    implementation (libs.accompanist.pager)
    implementation (libs.accompanist.pager.indicators)

    //Kotlinx Serialization for Type Safe Navigation
    implementation(libs.kotlinx.serialization.json)

    //Material Theme
    implementation (libs.material)

    //Accompanist system ui controller
    implementation (libs.accompanist.systemuicontroller)

    //Confetti Effect
    implementation (libs.konfetti)




}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
