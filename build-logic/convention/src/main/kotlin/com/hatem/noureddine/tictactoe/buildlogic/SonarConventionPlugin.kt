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
 * - Multi-branch support via GitBranchPlugin
 */
class SonarConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.sonarqube")
            pluginManager.apply("com.hatem.noureddine.tictactoe.gitBranch")

            extensions.configure<SonarExtension> {
                properties {
                    configureSonarProperties(target)
                }
            }

            // Make sonar task depend on detectGitBranch
            tasks.named("sonar") {
                dependsOn("detectGitBranch")
            }
        }
    }

    private fun org.sonarqube.gradle.SonarProperties.configureSonarProperties(target: Project) {
        val localProperties = loadLocalProperties(target)

        // Project identification
        configureProjectIdentification(target, localProperties)

        // Source configuration
        configureSourcePaths(target)

        // External analyzers
        configureExternalAnalyzers(target)

        // Coverage
        configureCoverage(target)

        // Exclusions
        configureExclusions()

        // Skip subprojects to avoid duplicate indexing
        skipSubprojects(target)
    }

    private fun skipSubprojects(target: Project) {
        target.subprojects.forEach { subproject ->
            subproject.extensions.findByType(SonarExtension::class.java)?.apply {
                isSkipProject = true
            }
        }
    }

    /**
     * Reads the branch name from git.properties file.
     * Run './gradlew detectGitBranch' first to generate the file.
     */
    private fun detectBranchName(target: Project): String? {
        val propsFile = target.rootProject.file("build/${GitBranchPlugin.PROPERTIES_FILE}")
        if (propsFile.exists()) {
            return try {
                val props = Properties()
                propsFile.inputStream().use { props.load(it) }
                props.getProperty(GitBranchPlugin.BRANCH_KEY)?.takeIf {
                    it.isNotBlank() && it != "unknown"
                }
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    private fun loadLocalProperties(target: Project): Properties {
        val properties = Properties()
        val file = target.rootProject.file("local.properties")
        if (file.exists()) {
            properties.load(file.inputStream())
        }
        return properties
    }

    private fun org.sonarqube.gradle.SonarProperties.configureProjectIdentification(
        target: Project,
        localProperties: Properties,
    ) {
        // Use takeIf to handle empty strings from environment variables
        val projectKey =
            System.getenv("SONAR_PROJECT_KEY")?.takeIf { it.isNotBlank() }
                ?: localProperties.getProperty("sonar.projectKey")?.takeIf { it.isNotBlank() }
                ?: "2026-DEV2-007-Hatem-NOUREDDINE_tictactoe"

        val organization =
            System.getenv("SONAR_ORGANIZATION_KEY")?.takeIf { it.isNotBlank() }
                ?: localProperties.getProperty("sonar.organization")?.takeIf { it.isNotBlank() }
                ?: "2026-dev2-007-hatem-noureddine"

        val token =
            System.getenv("SONAR_TOKEN")?.takeIf { it.isNotBlank() }
                ?: localProperties.getProperty("sonar.token")?.takeIf { it.isNotBlank() }

        property("sonar.projectKey", projectKey)
        property("sonar.organization", organization)
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sourceEncoding", "UTF-8")

        // Branch configuration for multi-branch analysis
        val branchName = detectBranchName(target)
        if (branchName != null) {
            property("sonar.branch.name", branchName)
        }

        if (token != null) {
            property("sonar.token", token)
        }

        // Android variant
        property("sonar.android.variant", "debug")
    }

    private fun org.sonarqube.gradle.SonarProperties.configureSourcePaths(target: Project) {
        val rootDir = target.rootProject.projectDir

        // Detect modules by checking for src/main/kotlin directories
        val sourcePaths = mutableListOf<String>()
        val testPaths = mutableListOf<String>()

        target.subprojects.forEach { module ->
            val relativePath = module.projectDir.relativeTo(rootDir).path
            val mainKotlin = File(rootDir, "$relativePath/src/main/kotlin")
            File(rootDir, "$relativePath/src/main/res")
            File(rootDir, "$relativePath/src/main/AndroidManifest.xml")
            val testKotlin = File(rootDir, "$relativePath/src/test/kotlin")
            val androidTestKotlin = File(rootDir, "$relativePath/src/androidTest/kotlin")

            // Add source paths if they exist - only Kotlin code
            // Skip res and AndroidManifest to avoid duplicate indexing issues
            // Android Lint reports will still analyze these files
            if (mainKotlin.exists()) {
                sourcePaths.add("$relativePath/src/main/kotlin")
            }

            // Add test paths if they exist
            if (testKotlin.exists()) {
                testPaths.add("$relativePath/src/test/kotlin")
            }
            if (androidTestKotlin.exists()) {
                testPaths.add("$relativePath/src/androidTest/kotlin")
            }
        }

        // Set project base directory
        property("sonar.projectBaseDir", rootDir.absolutePath)

        if (sourcePaths.isNotEmpty()) {
            property("sonar.sources", sourcePaths.joinToString(","))
        }
        if (testPaths.isNotEmpty()) {
            property("sonar.tests", testPaths.joinToString(","))
        }
    }

    private fun org.sonarqube.gradle.SonarProperties.configureExternalAnalyzers(target: Project) {
        val buildDir =
            target.rootProject.layout.buildDirectory
                .get()
        target.rootProject.projectDir

        // Detekt - use native merged report from DetektConventionPlugin
        val detektReport = "$buildDir/reports/detekt/detekt.xml"
        property("sonar.kotlin.detekt.reportPaths", detektReport)

        // Android Lint - discover reports by file existence
        val lintReports =
            target.subprojects.mapNotNull { module ->
                val reportPath = "${module.projectDir}/build/reports/lint-results-debug.xml"
                if (File(reportPath).exists()) reportPath else null
            }

        if (lintReports.isNotEmpty()) {
            property("sonar.androidLint.reportPaths", lintReports.joinToString(","))
        }

        // KtLint - discover reports by file existence
        val ktlintReports =
            target.subprojects.flatMap { module ->
                val ktlintDir = File("${module.projectDir}/build/reports/ktlint")
                if (ktlintDir.exists()) {
                    ktlintDir
                        .walkTopDown()
                        .filter { it.isFile && it.name.endsWith(".xml") && it.name.contains("Check") }
                        .map { it.absolutePath }
                        .toList()
                } else {
                    emptyList()
                }
            }

        if (ktlintReports.isNotEmpty()) {
            property("sonar.kotlin.ktlint.reportPaths", ktlintReports.joinToString(","))
        }

        // JUnit test reports - discover by file existence
        val testReports =
            target.subprojects.flatMap { module ->
                listOf(
                    "${module.projectDir}/build/test-results/testDebugUnitTest",
                    "${module.projectDir}/build/test-results/test",
                ).filter { File(it).exists() }
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
        // Exclude generated code and binary files from analysis
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
                // Binary files
                "**/*.webp",
                "**/*.png",
                "**/*.jpg",
                "**/*.jpeg",
                "**/*.gif",
                // Mipmap directories (icons)
                "**/mipmap-*/**",
            )

        property("sonar.exclusions", exclusions.joinToString(","))

        // Exclude UI and generated code from coverage calculation
        // These require instrumented tests which are not included in JaCoCo
        val coverageExclusions =
            listOf(
                "**/test/**",
                "**/androidTest/**",
                "**/ui/components/**",
                "**/ui/theme/**",
                "**/ui/GameScreen*",
                "**/ui/MainActivity*",
                "**/*Application*",
                "**/di/**",
            )
        property("sonar.coverage.exclusions", coverageExclusions.joinToString(","))
    }
}
