

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
    //id("org.jetbrains.kotlin.plugin.compose") version "1.5.11"
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
      //  classpath("com.google.dagger:hilt-android-gradle-plugin:2.51")
    }
}



