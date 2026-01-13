plugins {
    id("com.hatem.noureddine.tictactoe.library")
    id("com.hatem.noureddine.tictactoe.hilt")
    id("com.hatem.noureddine.tictactoe.detekt")
    id("com.hatem.noureddine.tictactoe.ktlint")
    id("com.hatem.noureddine.tictactoe.jacoco")
}

android {
    namespace = "com.hatem.noureddine.tictactoe.data"
}

dependencies {
    implementation(project(":domain"))
    // JUnit 5 for testing
    // Testing
    testImplementation(libs.bundles.testing.unit)
}
