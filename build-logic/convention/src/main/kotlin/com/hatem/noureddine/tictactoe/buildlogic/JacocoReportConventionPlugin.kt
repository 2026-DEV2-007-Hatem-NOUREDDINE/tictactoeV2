package com.hatem.noureddine.tictactoe.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Convention plugin that creates a merged JaCoCo report for the entire project.
 *
 * Applied at the root project level, this plugin:
 * - Aggregates coverage from all subprojects (Android and JVM modules)
 * - Collects both unit test (.exec) and instrumentation test (.ec) data
 * - Generates unified XML and HTML reports
 * - Only uses DEBUG variant to avoid release build issues
 *
 * Tasks created:
 * - jacocoTestReport: Unit tests coverage only (fast)
 * - jacocoInstrumentationReport: Instrumentation tests coverage only
 * - jacocoFullReport: Combined unit + instrumentation tests
 *
 * The merged report is used by SonarCloud for project-wide coverage analysis.
 */
class JacocoReportConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            if (this == rootProject) {
                configureRootProject()
            }
            configureTestTasks()
        }
    }

    private fun Project.configureRootProject() {
        pluginManager.apply("jacoco")

        extensions.configure<JacocoPluginExtension> {
            toolVersion = jacocoVersion()
        }

        val allSourceDirs = files(
            subprojects.flatMap { subproject ->
                JacocoConstants.SOURCE_DIRS.map { dir ->
                    subproject.layout.projectDirectory.dir(dir)
                }
            },
        )

        val allClassDirs = files(
            subprojects.map { subproject ->
                subproject.fileTree(
                    subproject.layout.buildDirectory
                        .dir(
                            if (subproject.isAndroidModule()) {
                                JacocoConstants.ANDROID_CLASS_DIR
                            } else {
                                JacocoConstants.JVM_CLASS_DIR
                            },
                        ).get()
                        .asFile,
                ) {
                    exclude(JacocoConstants.EXCLUSIONS)
                }
            },
        )

        // Unit test execution data only (.exec files from debug tests)
        val unitTestExecutionData = files(
            subprojects.map { subproject ->
                val buildDir = subproject.layout.buildDirectory.get()
                if (subproject.isAndroidModule()) {
                    buildDir.file(JacocoConstants.ExecutionData.UNIT_TEST_EXEC)
                } else {
                    buildDir.file(JacocoConstants.ExecutionData.JVM_TEST_EXEC)
                }
            },
        )

        // Instrumentation test execution data only (.ec files from debug tests)
        val instrumentationTestExecutionData = files(
            subprojects.filter { it.isAndroidModule() }.flatMap { subproject ->
                val buildDir = subproject.layout.buildDirectory.get()
                listOf(
                    subproject.fileTree(
                        buildDir.dir(JacocoConstants.ExecutionData.CONNECTED_TEST_DIR).asFile,
                    ) { include("*.ec") },
                    subproject.fileTree(
                        buildDir.dir(JacocoConstants.ExecutionData.MANAGED_DEVICE_DIR).asFile,
                    ) { include("*.ec") },
                    subproject.fileTree(
                        buildDir.dir(JacocoConstants.ExecutionData.MANAGED_DEVICE_ADDITIONAL_DIR).asFile,
                    ) { include("*.ec") },
                )
            },
        )

        // Task 1: Unit tests coverage only (fast, default for CI)
        tasks.register<JacocoReport>("jacocoTestReport") {
            // Only depend on debug variant tests for Android modules
            // and standard test task for JVM modules
            dependsOn(
                subprojects.mapNotNull { subproject ->
                    if (subproject.isAndroidModule()) {
                        subproject.tasks.findByPath("testDebugUnitTest")
                    } else {
                        subproject.tasks.findByPath("test")
                    }
                },
            )

            group = "Reporting"
            description = "Generate merged JaCoCo coverage report for unit tests (debug only)."

            reports {
                xml.required.set(true)
                html.required.set(true)
            }

            sourceDirectories.setFrom(allSourceDirs)
            classDirectories.setFrom(allClassDirs)
            executionData.setFrom(unitTestExecutionData)
        }

        // Task 2: Instrumentation tests coverage only (requires emulator/device)
        tasks.register<JacocoReport>("jacocoInstrumentationReport") {
            // Depend on debug instrumentation tests only
            dependsOn(
                subprojects.filter { it.isAndroidModule() }.mapNotNull { subproject ->
                    subproject.tasks.findByPath("connectedDebugAndroidTest")
                        ?: subproject.tasks.findByPath("pixel2DebugAndroidTest")
                },
            )

            group = "Reporting"
            description = "Generate JaCoCo coverage report for instrumentation tests (debug only)."

            reports {
                xml.required.set(true)
                html.required.set(true)
            }

            sourceDirectories.setFrom(allSourceDirs)
            classDirectories.setFrom(allClassDirs)
            executionData.setFrom(instrumentationTestExecutionData)
        }

        // Task 3: Full coverage report (unit + instrumentation)
        tasks.register<JacocoReport>("jacocoFullReport") {
            dependsOn("jacocoTestReport", "jacocoInstrumentationReport")

            group = "Reporting"
            description = "Generate merged JaCoCo coverage report for all tests (unit + instrumentation, debug only)."

            reports {
                xml.required.set(true)
                html.required.set(true)
            }

            sourceDirectories.setFrom(allSourceDirs)
            classDirectories.setFrom(allClassDirs)
            executionData.setFrom(files(unitTestExecutionData, instrumentationTestExecutionData))
        }
    }

    private fun Project.configureTestTasks() {
        pluginManager.apply("jacoco")

        listOf("java-library", "com.android.library", "com.android.application").forEach { pluginId ->
            plugins.withId(pluginId) {
                tasks.withType<Test> {
                    configure<JacocoTaskExtension> {
                        isIncludeNoLocationClasses = true
                        excludes = listOf("jdk.internal.*")
                    }
                }
            }
        }
    }

    private fun Project.isAndroidModule(): Boolean =
        plugins.hasPlugin("com.android.application") || plugins.hasPlugin("com.android.library")
}

