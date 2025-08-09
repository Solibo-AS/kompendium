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
  val kotlinVersion: String by project
  val kotlinSerializeVersion: String by project

  // Kompendium
  api(projects.soliboKompendiumEnrichment)

  implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializeVersion")

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
