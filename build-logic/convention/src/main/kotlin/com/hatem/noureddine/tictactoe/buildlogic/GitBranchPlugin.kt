package com.hatem.noureddine.tictactoe.buildlogic

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.util.Properties
import javax.inject.Inject

/**
 * Plugin that detects the current Git branch and saves it to a properties file.
 *
 * The branch detection runs at EXECUTION time via the `detectGitBranch` task,
 * which writes the result to `build/git.properties`.
 *
 * Other plugins can read this file to get the branch name.
 *
 * Usage:
 * ```bash
 * ./gradlew detectGitBranch  # Writes branch to build/git.properties
 * ./gradlew sonar            # Reads from build/git.properties
 * ```
 */
class GitBranchPlugin : Plugin<Project> {
    companion object {
        const val PROPERTIES_FILE = "git.properties"
        const val BRANCH_KEY = "git.branch"
    }

    override fun apply(target: Project) {
        val extension =
            target.extensions.create(
                "gitBranch",
                GitBranchExtension::class.java,
            )

        // Configure the output file location
        val gitPropertiesFile = target.layout.buildDirectory.file(PROPERTIES_FILE)
        extension.propertiesFile.convention(gitPropertiesFile)

        // Register task to detect git branch and write to file
        target.tasks.register("detectGitBranch", DetectGitBranchTask::class.java) {
            group = "versioning"
            description = "Detects the current Git branch and saves to ${PROPERTIES_FILE}"
            outputFile.convention(gitPropertiesFile)
        }

        // Register a task to print the branch (useful for debugging)
        target.tasks.register("printGitBranch") {
            group = "help"
            description = "Prints the current Git branch name from ${PROPERTIES_FILE}"
            doLast {
                val file = gitPropertiesFile.get().asFile
                if (file.exists()) {
                    val props = Properties()
                    file.inputStream().use { props.load(it) }
                    println("Current branch: ${props.getProperty(BRANCH_KEY) ?: "unknown"}")
                } else {
                    println("Branch not detected. Run './gradlew detectGitBranch' first.")
                }
            }
        }

        // Read branch from properties file during configuration (if exists)
        val propsFile = target.rootProject.file("build/${PROPERTIES_FILE}")
        if (propsFile.exists()) {
            try {
                val props = Properties()
                propsFile.inputStream().use { props.load(it) }
                props.getProperty(BRANCH_KEY)?.let {
                    extension.branchName.set(it)
                }
            } catch (e: Exception) {
                // Ignore read errors
            }
        }
    }
}

/**
 * Task that detects the current Git branch and writes it to a properties file.
 */
abstract class DetectGitBranchTask : DefaultTask() {
    @get:Inject
    abstract val execOperations: ExecOperations

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun detect() {
        val branch = detectBranch()
        val file = outputFile.get().asFile

        file.parentFile?.mkdirs()

        val props = Properties()
        props.setProperty(GitBranchPlugin.BRANCH_KEY, branch ?: "unknown")
        file.outputStream().use {
            props.store(it, "Git branch information - auto-generated")
        }

        logger.lifecycle("Git branch '${branch ?: "unknown"}' written to ${file.path}")
    }

    private fun detectBranch(): String? =
        try {
            val output = ByteArrayOutputStream()
            execOperations.exec {
                commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
                standardOutput = output
                isIgnoreExitValue = true
            }
            val branch = output.toString().trim()
            if (branch.isNotBlank() && branch != "HEAD") branch else null
        } catch (e: Exception) {
            logger.warn("Failed to detect git branch: ${e.message}")
            null
        }
}

/**
 * Extension providing access to the current Git branch name.
 */
abstract class GitBranchExtension {
    /**
     * The current branch name read from properties file.
     */
    abstract val branchName: Property<String>

    /**
     * Path to the git properties file.
     */
    abstract val propertiesFile: RegularFileProperty
}
