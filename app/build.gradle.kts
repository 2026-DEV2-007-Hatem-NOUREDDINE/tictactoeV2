plugins {
    id("com.hatem.noureddine.tictactoe.application")
    id("com.hatem.noureddine.tictactoe.compose")
    id("com.hatem.noureddine.tictactoe.hilt")
    id("com.hatem.noureddine.tictactoe.jacoco")
    id("com.hatem.noureddine.tictactoe.detekt")
    id("com.hatem.noureddine.tictactoe.ktlint")
    id("com.hatem.noureddine.tictactoe.roborazzi")
}

android {
    namespace = "com.hatem.noureddine.tictactoe"

    defaultConfig {
        applicationId = "com.hatem.noureddine.tictactoe"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Clear app state between tests
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }

        managedDevices {
            localDevices {
                // Primary device: Smallest screen, ATD image (fastest for CI)
                create("pixel2") {
                    device = "Pixel 2"
                    apiLevel = 30
                    systemImageSource = "aosp-atd" // ATD = Automated Test Device (smaller, faster)
                }
            }
            groups {
                create("ci") {
                    targetDevices.add(localDevices["pixel2"])
                }
            }
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.bundles.lifecycle)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.material)
    implementation(libs.androidx.material.icons.extended)

    testImplementation(libs.bundles.testing.unit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.androidx.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.testing.android)

    debugImplementation(libs.bundles.compose.debug)
}
