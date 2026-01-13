package com.hatem.noureddine.tictactoe.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk =
            versionCatalog()
                .findVersion("compileSdk")
                .get()
                .toString()
                .toInt()

        defaultConfig {
            minSdk =
                versionCatalog()
                    .findVersion("minSdk")
                    .get()
                    .toString()
                    .toInt()
        }

        compileOptions {
            sourceCompatibility = org.gradle.api.JavaVersion.VERSION_21
            targetCompatibility = org.gradle.api.JavaVersion.VERSION_21
        }
    }

    // Configure Kotlin
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            // Treat all Kotlin warnings as errors (disabled by default)
            // allWarningsAsErrors = properties["warningsAsErrors"] as? Boolean ?: false
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
            )
        }
    }
}
