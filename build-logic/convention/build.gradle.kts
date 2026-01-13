import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.hatem.noureddine.tictactoe.buildlogic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
    implementation(libs.ktlint.gradlePlugin)
    implementation(libs.compose.compiler.gradlePlugin)
    implementation(libs.hilt.android.gradlePlugin)
    implementation(libs.sonarqube.gradlePlugin)
    implementation(libs.roborazzi.gradlePlugin)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "com.hatem.noureddine.tictactoe.application"
            implementationClass = "com.hatem.noureddine.tictactoe.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "com.hatem.noureddine.tictactoe.library"
            implementationClass = "com.hatem.noureddine.tictactoe.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("jvmLibrary") {
            id = "com.hatem.noureddine.tictactoe.jvm.library"
            implementationClass = "com.hatem.noureddine.tictactoe.buildlogic.JvmLibraryConventionPlugin"
        }
        register("detekt") {
            id = "com.hatem.noureddine.tictactoe.detekt"
            implementationClass = "com.hatem.noureddine.tictactoe.buildlogic.DetektConventionPlugin"
        }
        register("ktlint") {
            id = "com.hatem.noureddine.tictactoe.ktlint"
            implementationClass = "com.hatem.noureddine.tictactoe.buildlogic.KtLintConventionPlugin"
        }
        register("hilt") {
            id = "com.hatem.noureddine.tictactoe.hilt"
            implementationClass = "com.hatem.noureddine.tictactoe.buildlogic.HiltConventionPlugin"
        }
        register("compose") {
            id = "com.hatem.noureddine.tictactoe.compose"
            implementationClass = "com.hatem.noureddine.tictactoe.buildlogic.ComposeConventionPlugin"
        }
        register("jacoco") {
            id = "com.hatem.noureddine.tictactoe.jacoco"
            implementationClass = "com.hatem.noureddine.tictactoe.buildlogic.JacocoConventionPlugin"
        }
        register("sonarqube") {
            id = "com.hatem.noureddine.tictactoe.sonarqube"
            implementationClass = "com.hatem.noureddine.tictactoe.buildlogic.SonarConventionPlugin"
        }
        register("roborazzi") {
            id = "com.hatem.noureddine.tictactoe.roborazzi"
            implementationClass = "com.hatem.noureddine.tictactoe.buildlogic.RoborazziConventionPlugin"
        }
        register("jacocoReport") {
            id = "com.hatem.noureddine.tictactoe.jacoco.report"
            implementationClass = "com.hatem.noureddine.tictactoe.buildlogic.JacocoReportConventionPlugin"
        }
    }
}
