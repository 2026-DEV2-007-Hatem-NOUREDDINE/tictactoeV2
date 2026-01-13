package com.hatem.noureddine.tictactoe.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Extension to access the version catalog from convention plugins.
 */
internal val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Get a version string from the version catalog.
 * Returns the version if found, otherwise returns the default value.
 */
internal fun VersionCatalog.version(
    alias: String,
    default: String = "",
): String = findVersion(alias).map { it.requiredVersion }.orElse(default)

/**
 * Get JaCoCo version from version catalog with a sensible default.
 */
internal fun Project.jacocoVersion(): String =
    try {
        libs.version("jacoco", "0.8.14")
    } catch (e: Exception) {
        "0.8.14"
    }
