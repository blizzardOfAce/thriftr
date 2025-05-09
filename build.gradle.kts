// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false

    //Hilt
    id("com.google.dagger.hilt.android") version "2.52" apply false

    //Kotlinx Serialization
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"

}

//AppWrite
// repositories
// {
//    mavenCentral()
//}
//but it was already added in settings.gradle.kts
