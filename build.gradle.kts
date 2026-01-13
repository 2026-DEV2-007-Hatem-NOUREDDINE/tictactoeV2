plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.internal.sonarqube)
    alias(libs.plugins.internal.jacoco)
}

// Task to copy Git hooks
val copyGitHooks =
    tasks.register<Copy>("copyGitHooks") {
        description = "Copies Git hooks from config/git-hooks to .git/hooks"
        group = "git hooks"
        from("$projectDir/config/git-hooks")
        into("$projectDir/.git/hooks")
    }

// Task to make hooks executable
tasks.register<Exec>("installGitHooks") {
    description = "Installs Git hooks and makes them executable"
    group = "git hooks"
    dependsOn(copyGitHooks)
    commandLine("chmod", "-R", "+x", ".git/hooks/")
    doLast {
        println("✅ Git hooks installed successfully.")
    }
}

// Task to remove Git hooks
tasks.register<Delete>("uninstallGitHooks") {
    description = "Removes Git hooks from .git/hooks"
    group = "git hooks"
    delete(
        "$projectDir/.git/hooks/pre-commit",
        "$projectDir/.git/hooks/commit-msg",
        "$projectDir/.git/hooks/pre-push",
    )
    doLast {
        println("✅ Git hooks uninstalled successfully.")
    }
}

// Ensure hooks are installed when syncing Gradle
tasks.named("prepareKotlinBuildScriptModel") { dependsOn("installGitHooks") }
