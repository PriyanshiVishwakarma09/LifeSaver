

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
    id ("com.google.dagger.hilt.android") version "2.51" apply false
    id ("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
      //  classpath("com.google.dagger:hilt-android-gradle-plugin:2.51")
    }
}



