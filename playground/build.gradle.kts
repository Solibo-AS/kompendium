plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("io.bkbn.sourdough.application.jvm")
  id("application")
  id("org.jetbrains.kotlinx.kover")
}

sourdoughApp {
  compilerArgs.set(listOf("-opt-in=kotlin.RequiresOptIn"))
}

dependencies {
  // IMPLEMENTATION
  implementation(projects.soliboKompendiumCore)
  implementation(projects.soliboKompendiumResources)
  implementation(projects.soliboKompendiumProtobufJavaConverter)

  // Versions
  val detektVersion: String by project

  // Ktor
  val kotlinSerializeVersion: String by project
  val ktorVersion: String by project

  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-cio:$ktorVersion")
  implementation("io.ktor:ktor-server-auth:$ktorVersion")
  implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
  implementation("io.ktor:ktor-serialization:$ktorVersion")
  implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
  implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
  implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
  implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
  implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
  implementation("io.ktor:ktor-server-resources:$ktorVersion")

  // Logging
  implementation("org.apache.logging.log4j:log4j-api-kotlin:1.5.0")
  implementation("org.apache.logging.log4j:log4j-api:2.25.1")
  implementation("org.apache.logging.log4j:log4j-core:2.25.1")
  implementation("org.slf4j:slf4j-api:2.0.17")
  implementation("org.slf4j:slf4j-simple:2.0.17")

  // YAML
  implementation("com.charleskorn.kaml:kaml:0.83.0")

  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializeVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")

  implementation("joda-time:joda-time:2.14.0")

  // Formatting
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}
