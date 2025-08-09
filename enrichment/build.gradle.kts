plugins {
  kotlin("jvm")
  id("io.gitlab.arturbosch.detekt")
  id("com.adarshr.test-logger")
  id("java-library")
  id("org.jetbrains.kotlinx.kover")
}

dependencies {
  // Versions
  val detektVersion: String by project

  // Formatting
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

  testImplementation(testFixtures(projects.soliboKompendiumCore))
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
