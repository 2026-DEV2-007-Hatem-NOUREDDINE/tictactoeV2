package com.hatem.noureddine.tictactoe.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Convention plugin that configures JaCoCo for code coverage reporting.
 *
 * For Android modules: Creates `testDebugUnitTestCoverage` and `verifyJacocoCoverage` tasks.
 * For JVM modules: Creates `testCoverage` task.
 *
 * Features:
 * - Comprehensive exclusions for generated code (Hilt, Compose, Room, etc.)
 * - Unit test and instrumentation test coverage collection
 * - Coverage verification with configurable thresholds
 * - XML, HTML, and CSV report generation
 */
class JacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("jacoco")

            extensions.configure<JacocoPluginExtension> {
                toolVersion = jacocoVersion()
            }

            tasks.withType<Test> {
                configure<JacocoTaskExtension> {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                }
            }

            val sourceDirectories =
                JacocoConstants.SOURCE_DIRS.map {
                    project.layout.projectDirectory.dir(it)
                }

            if (isAndroidModule()) {
                configureAndroidJacoco(sourceDirectories)
            } else {
                configureJvmJacoco(sourceDirectories)
            }
        }
    }

    private fun Project.isAndroidModule(): Boolean =
        pluginManager.hasPlugin("com.android.library") ||
            pluginManager.hasPlugin("com.android.application")

    private fun Project.configureAndroidJacoco(sourceDirectories: List<org.gradle.api.file.Directory>) {
        val classDirectories =
            fileTree(
                layout.buildDirectory
                    .dir(JacocoConstants.ANDROID_CLASS_DIR)
                    .get()
                    .asFile,
            ) {
                exclude(JacocoConstants.EXCLUSIONS)
            }

        val executionDataFiles =
            files(
                layout.buildDirectory
                    .file(JacocoConstants.ExecutionData.UNIT_TEST_EXEC)
                    .get()
                    .asFile,
                fileTree(
                    layout.buildDirectory
                        .dir(JacocoConstants.ExecutionData.CONNECTED_TEST_DIR)
                        .get()
                        .asFile,
                ) {
                    include("*.ec")
                },
                fileTree(
                    layout.buildDirectory
                        .dir(JacocoConstants.ExecutionData.MANAGED_DEVICE_DIR)
                        .get()
                        .asFile,
                ) {
                    include("*.ec")
                },
                fileTree(
                    layout.buildDirectory
                        .dir(JacocoConstants.ExecutionData.MANAGED_DEVICE_ADDITIONAL_DIR)
                        .get()
                        .asFile,
                ) {
                    include("*.ec")
                },
            )

        tasks.register<JacocoReport>("testDebugUnitTestCoverage") {
            dependsOn("testDebugUnitTest")
            group = "Reporting"
            description = "Generate JaCoCo coverage reports for the debug build."

            reports {
                xml.required.set(true)
                html.required.set(true)
            }

            this.sourceDirectories.setFrom(files(sourceDirectories))
            this.classDirectories.setFrom(files(classDirectories))
            this.executionData.setFrom(executionDataFiles)
        }

        tasks.register<JacocoCoverageVerification>("verifyJacocoCoverage") {
            dependsOn("testDebugUnitTestCoverage")
            group = "Verification"
            description = "Verify JaCoCo coverage meets minimum thresholds."

            this.sourceDirectories.setFrom(files(sourceDirectories))
            this.classDirectories.setFrom(files(classDirectories))
            this.executionData.setFrom(
                files(
                    layout.buildDirectory
                        .file(JacocoConstants.ExecutionData.UNIT_TEST_EXEC)
                        .get()
                        .asFile,
                ),
            )

            violationRules {
                rule {
                    limit {
                        minimum = "0.8".toBigDecimal()
                    }
                }
            }
        }
    }

    private fun Project.configureJvmJacoco(sourceDirectories: List<org.gradle.api.file.Directory>) {
        val classDirectories =
            fileTree(
                layout.buildDirectory
                    .dir(JacocoConstants.JVM_CLASS_DIR)
                    .get()
                    .asFile,
            ) {
                exclude(JacocoConstants.EXCLUSIONS)
            }

        tasks.register<JacocoReport>("testCoverage") {
            dependsOn("test")
            group = "Reporting"
            description = "Generate JaCoCo coverage reports for JVM module."

            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(true)
            }

            this.sourceDirectories.setFrom(files(sourceDirectories))
            this.classDirectories.setFrom(files(classDirectories))
            this.executionData.setFrom(
                files(
                    layout.buildDirectory
                        .file(JacocoConstants.ExecutionData.JVM_TEST_EXEC)
                        .get()
                        .asFile,
                ),
            )
        }
    }
}
