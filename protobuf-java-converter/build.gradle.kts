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

  implementation(projects.soliboKompendiumJsonSchema)
  implementation("com.google.protobuf:protobuf-java:3.25.6")
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

