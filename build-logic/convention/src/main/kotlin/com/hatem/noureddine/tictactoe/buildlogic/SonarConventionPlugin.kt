package com.hatem.noureddine.tictactoe.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.sonarqube.gradle.SonarExtension
import java.io.File
import java.util.Properties

/**
 * Convention plugin that configures SonarCloud analysis.
 *
 * Features:
 * - Dynamic module discovery (no hardcoded module list)
 * - Integration with JaCoCo, Detekt, KtLint, and Android Lint reports
 * - Support for both CI (environment variables) and local (local.properties) configuration
 * - Automatic exclusion of generated code
 */
class SonarConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.sonarqube")

            extensions.configure<SonarExtension> {
                properties {
                    configureSonarProperties(target)
                }
            }
        }
    }

    private fun org.sonarqube.gradle.SonarProperties.configureSonarProperties(target: Project) {
        val localProperties = loadLocalProperties(target)

        // Project identification
        configureProjectIdentification(localProperties)

        // Source configuration
        configureSourcePaths(target)

        // External analyzers
        configureExternalAnalyzers(target)

        // Coverage
        configureCoverage(target)

        // Exclusions
        configureExclusions()
    }

    private fun loadLocalProperties(target: Project): Properties {
        val properties = Properties()
        val file = target.rootProject.file("local.properties")
        if (file.exists()) {
            properties.load(file.inputStream())
        }
        return properties
    }

    private fun org.sonarqube.gradle.SonarProperties.configureProjectIdentification(localProperties: Properties) {
        val projectKey =
            System.getenv("SONAR_PROJECT_KEY")
                ?: localProperties.getProperty("sonar.projectKey")
                ?: "2026-DEV2-007-Hatem-NOUREDDINE_tictactoe"

        val organization =
            System.getenv("SONAR_ORGANIZATION_KEY")
                ?: localProperties.getProperty("sonar.organization")
                ?: "2026-dev2-007-hatem-noureddine"

        val token =
            System.getenv("SONAR_TOKEN")
                ?: localProperties.getProperty("sonar.token")

        property("sonar.projectKey", projectKey)
        property("sonar.organization", organization)
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sourceEncoding", "UTF-8")

        if (token != null) {
            property("sonar.login", token)
        }

        // Android variant
        property("sonar.android.variant", "debug")
    }

    private fun org.sonarqube.gradle.SonarProperties.configureSourcePaths(target: Project) {
        val modules =
            target.subprojects.filter { subproject ->
                subproject.plugins.hasPlugin("com.android.application") ||
                    subproject.plugins.hasPlugin("com.android.library") ||
                    subproject.plugins.hasPlugin("java-library") ||
                    subproject.plugins.hasPlugin("org.jetbrains.kotlin.jvm")
            }

        val sourcePaths =
            modules.flatMap { module ->
                listOf(
                    "${module.projectDir}/src/main/java",
                    "${module.projectDir}/src/main/kotlin",
                ).filter { File(it).exists() }
            }

        val testPaths =
            modules.flatMap { module ->
                listOf(
                    "${module.projectDir}/src/test/java",
                    "${module.projectDir}/src/test/kotlin",
                    "${module.projectDir}/src/androidTest/java",
                    "${module.projectDir}/src/androidTest/kotlin",
                ).filter { File(it).exists() }
            }

        if (sourcePaths.isNotEmpty()) {
            property("sonar.sources", sourcePaths.joinToString(","))
        }
        if (testPaths.isNotEmpty()) {
            property("sonar.tests", testPaths.joinToString(","))
        }
    }

    private fun org.sonarqube.gradle.SonarProperties.configureExternalAnalyzers(target: Project) {
        // Detekt
        val detektReport = "${target.rootProject.layout.buildDirectory.get()}/reports/detekt/detekt.xml"
        property("sonar.kotlin.detekt.reportPaths", detektReport)

        // Android Lint (dynamic discovery)
        val lintReports =
            target.subprojects
                .filter { it.plugins.hasPlugin("com.android.application") || it.plugins.hasPlugin("com.android.library") }
                .map { "${it.projectDir}/build/reports/lint-results-debug.xml" }
                .filter { File(it).exists() } // Include even if not yet generated

        if (lintReports.isNotEmpty()) {
            property("sonar.androidLint.reportPaths", lintReports.joinToString(","))
        }

        // KtLint (dynamic discovery)
        val ktlintReports =
            target.subprojects.flatMap { module ->
                val ktlintDir = "${module.projectDir}/build/reports/ktlint"
                listOf(
                    "$ktlintDir/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.xml",
                    "$ktlintDir/ktlintTestSourceSetCheck/ktlintTestSourceSetCheck.xml",
                )
            }

        if (ktlintReports.isNotEmpty()) {
            property("sonar.kotlin.ktlint.reportPaths", ktlintReports.joinToString(","))
        }

        // JUnit test reports (dynamic discovery)
        val testReports =
            target.subprojects.flatMap { module ->
                listOf(
                    "${module.projectDir}/build/test-results/testDebugUnitTest",
                    "${module.projectDir}/build/test-results/test",
                )
            }

        if (testReports.isNotEmpty()) {
            property("sonar.junit.reportPaths", testReports.joinToString(","))
        }
    }

    private fun org.sonarqube.gradle.SonarProperties.configureCoverage(target: Project) {
        // Merged JaCoCo report from root project
        val mergedReport = "${target.rootProject.projectDir}/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"
        property("sonar.coverage.jacoco.xmlReportPaths", mergedReport)
    }

    private fun org.sonarqube.gradle.SonarProperties.configureExclusions() {
        // Exclude generated code from analysis
        val exclusions =
            listOf(
                "**/R.java",
                "**/R$*.java",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "**/*_Hilt*.java",
                "**/Hilt_*.java",
                "**/*_Factory.java",
                "**/*_MembersInjector.java",
                "**/Dagger*.java",
                "**/*Module_*.java",
                "**/*_Impl.java",
                "**/databinding/**",
                "**/generated/**",
            )

        property("sonar.exclusions", exclusions.joinToString(","))

        // Exclude test classes from coverage calculation
        property("sonar.coverage.exclusions", "**/test/**,**/androidTest/**")
    }
}
