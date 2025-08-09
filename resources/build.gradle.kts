plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("io.gitlab.arturbosch.detekt")
  id("com.adarshr.test-logger")
  id("java-library")
  id("org.jetbrains.kotlinx.kover")
}

dependencies {
  // Versions
  val detektVersion: String by project
  val ktorVersion: String by project

  // IMPLEMENTATION

  implementation(projects.soliboKompendiumCore)
  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-resources:$ktorVersion")

  // TESTING

  testImplementation(testFixtures(projects.soliboKompendiumCore))

  // Formatting
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

testing {
  suites {
    named("test", JvmTestSuite::class) {
      useJUnitJupiter()
    }
  }
}
