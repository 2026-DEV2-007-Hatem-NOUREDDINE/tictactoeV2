package com.hatem.noureddine.tictactoe.buildlogic

/**
 * Centralized JaCoCo exclusion patterns for consistent coverage reporting.
 * These patterns exclude generated code, framework internals, and test classes.
 */
object JacocoConstants {
    /**
     * Common exclusion patterns for JaCoCo coverage reports.
     */
    val EXCLUSIONS = listOf(
        // Android generated
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "android/**/*.*",

        // Kotlin/Java internals
        "**/*\$Lambda\$*.*",
        "**/*\$inlined\$*.*",
        "**/Lambda.class",
        "**/Lambda\$*.class",

        // Test classes
        "**/*Test*.*",
        "**/*Tests*.*",
        "**/*Spec*.*",

        // Hilt/Dagger generated
        "**/*Hilt*.*",
        "**/hilt_aggregated_deps/**",
        "**/*_Factory.class",
        "**/*_Factory\$*.*",
        "**/*_MembersInjector.class",
        "**/*_MembersInjector\$*.*",
        "**/*Module_*Factory.class",
        "**/*_ComponentTreeDeps.class",
        "**/*_HiltModules*.*",
        "**/Hilt_*.*",
        "**/*_GeneratedInjector.*",

        // Compose generated
        "**/*ComposableSingletons*.*",
        "**/*\$ComposableLambda\$*.*",
        "**/*Kt\$*.*",

        // Data classes (models/DTOs - optional, uncomment if needed)
        // "**/data/models/*",
        // "**/dto/*",
        // "**/entity/*",

        // Navigation generated
        "**/navigation/*Args*.*",
        "**/navigation/*Directions*.*",

        // Room generated
        "**/*_Impl.class",
        "**/*_Impl\$*.class",

        // Sealed/Enum classes internals
        "**/*\$WhenMappings.*",
        "**/*\$Companion.*",

        // Parcelable
        "**/*\$Creator.*",
        "**/*\$Parcel.*",
    )

    /**
     * Source directories to include in coverage reports.
     */
    val SOURCE_DIRS = listOf(
        "src/main/java",
        "src/main/kotlin",
    )

    /**
     * Android class directories for debug builds.
     */
    const val ANDROID_CLASS_DIR = "tmp/kotlin-classes/debug"

    /**
     * JVM class directories.
     */
    const val JVM_CLASS_DIR = "classes/kotlin/main"

    /**
     * Execution data paths for different test types.
     */
    object ExecutionData {
        const val UNIT_TEST_EXEC = "jacoco/testDebugUnitTest.exec"
        const val JVM_TEST_EXEC = "jacoco/test.exec"

        // Instrumentation test directories (contains .ec files)
        const val CONNECTED_TEST_DIR = "outputs/code_coverage/debugAndroidTest/connected/"
        const val MANAGED_DEVICE_DIR = "outputs/managed_device_code_coverage/debug/"
        const val MANAGED_DEVICE_ADDITIONAL_DIR = "outputs/managed_device_android_test_additional_output/debug/"
    }
}
