
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()  // Google's Maven repository
        mavenCentral()  // Maven Central repository
    }
}


plugins {
    // This line seems unnecessary unless specific plugins need to be applied conditionally or globally
    alias(libs.plugins.android.application) apply false
}
