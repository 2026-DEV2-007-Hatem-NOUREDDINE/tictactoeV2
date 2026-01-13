plugins {
    id("com.hatem.noureddine.tictactoe.jvm.library")
    id("com.hatem.noureddine.tictactoe.detekt")
    id("com.hatem.noureddine.tictactoe.ktlint")
    id("com.hatem.noureddine.tictactoe.jacoco")
}

dependencies {
    implementation(libs.javax.inject)

    // JUnit 5 for testing
    // Testing
    testImplementation(libs.bundles.testing.unit)
}
