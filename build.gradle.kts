import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.TestLoggerPlugin
import com.adarshr.gradle.testlogger.theme.ThemeType
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.plugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

val buildUtils = BuildUtils()
val branchName = buildUtils.gitBranch()

plugins {
  kotlin("jvm") version "2.1.20" apply false
  kotlin("plugin.serialization") version "2.1.20" apply false
  id("io.bkbn.sourdough.application.jvm") version "0.13.1" apply false
  id("io.bkbn.sourdough.root") version "0.13.1"
  id("org.jetbrains.kotlinx.kover") version "0.9.1"

  `maven-publish`
  signing
}

dependencies {
  subprojects.forEach { kover(it) }
}

allprojects {
  group = "no.solibo.kompendium"
  version = buildUtils.calculateVersion(properties["version"] as String, branchName)
}

subprojects {
  apply {
    plugin<KotlinPluginWrapper>()
    plugin<SigningPlugin>()
    plugin<PublishingPlugin>()
    plugin<IdeaPlugin>()
    plugin<MavenPublishPlugin>()
    plugin<DetektPlugin>()
    plugin<KoverGradlePlugin>()
  }

  signing {
    useGpgCmd()

    sign(publishing.publications)
  }

  configure<DetektExtension> {
    config.setFrom("${rootProject.projectDir}/detekt.yml")
    buildUponDefaultConfig = true
    autoCorrect = true
  }

  configure<KoverProjectExtension> {
    useJacoco()
  }

  plugins.withType<TestLoggerPlugin> {
    extensions.configure(TestLoggerExtension::class.java) {
      theme = ThemeType.MOCHA
      logLevel = LogLevel.LIFECYCLE
      showExceptions = true
      showStackTraces = true
      showFullStackTraces = false
      showCauses = true
      slowThreshold = 2000
      showSummary = true
      showSimpleNames = false
      showPassed = true
      showSkipped = true
      showFailed = true
      showStandardStreams = false
      showPassedStandardStreams = true
      showSkippedStandardStreams = true
      showFailedStandardStreams = true
    }
  }

  publishing {
    repositories {
      mavenLocal()

      if (branchName == "develop" && project.hasProperty("deploy")) {
        maven(url = "https://nexus-827992082019.d.codeartifact.eu-west-1.amazonaws.com/maven/libs-snapshot/") {
          credentials {
            username = "aws"
            password = buildUtils.getCodeArtifactPassword("nexus", "827992082019")
          }
        }
      } else if (branchName == "main" && project.hasProperty("deploy")) {
        maven(url = "https://nexus-827992082019.d.codeartifact.eu-west-1.amazonaws.com/maven/libs/") {
          credentials {
            username = "aws"
            password = buildUtils.getCodeArtifactPassword("nexus", "827992082019")
          }
        }
      }
    }

    publications {
      create<MavenPublication>("mavenJava") {
        from(components["java"])
      }
    }
  }
}

class BuildUtils {
  fun calculateVersion(version: String, branch: String): String {
    return when {
      branch == "develop" -> "$version-SNAPSHOT"
      branch == "main" -> version
      branch.startsWith("release", ignoreCase = true) -> version
      else -> "$version-$branch"
    }
  }

  fun gitBranch(): String {
    @Suppress("DEPRECATION")
    return System.getenv("BRANCH_NAME") ?: Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD")
      .inputStream
      .bufferedReader()
      .readText()
      .trim()
  }

  fun getCodeArtifactPassword(domain: String, domainOwner: String): String {
    val builder = ProcessBuilder().command(
      "aws",
      "codeartifact",
      "get-authorization-token",
      "--domain",
      domain,
      "--domain-owner",
      domainOwner,
      "--query",
      "authorizationToken",
      "--output",
      "text",
    )
    val proc = builder.start()
    val time = 15L
    val timeUnit = TimeUnit.SECONDS
    val finished = proc.waitFor(time, timeUnit)

    return when {
      !finished -> throw IllegalStateException("Login process did not finished with in $time-$timeUnit")
      proc.exitValue() != 0 -> {
        val error = proc.errorStream.bufferedReader().readText()

        throw IllegalStateException("Could not log into AWS CodeArtifact: ${proc.exitValue()} \n $error")
      }
      else -> proc.inputStream.bufferedReader().readText()
    }
  }
}
