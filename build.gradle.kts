import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.TestLoggerPlugin
import com.adarshr.gradle.testlogger.theme.ThemeType
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

val buildUtils = BuildUtils()
val branchName = buildUtils.gitBranch()

repositories {
  mavenCentral()
}

plugins {
  kotlin("jvm") version "2.2.0" apply false
  kotlin("plugin.serialization") version "2.2.0" apply false

  id("io.bkbn.sourdough.application.jvm") version "0.13.1" apply false
  id("io.bkbn.sourdough.root") version "0.13.1"
  id("org.jetbrains.kotlinx.kover") version "0.9.1"
  id("com.github.ben-manes.versions") version "0.52.0"
  id("org.jetbrains.dokka") version "2.0.0"
  id("com.vanniktech.maven.publish") version "0.34.0"
}

dependencies {
  subprojects.forEach { kover(it) }
}

allprojects {
  group = "no.solibo.oss"
  version = buildUtils.calculateVersion(properties["version"] as String, branchName)
}

subprojects {
  apply {
    plugin<JavaLibraryPlugin>()
    plugin<KotlinPluginWrapper>()
    plugin<IdeaPlugin>()
    plugin<DetektPlugin>()
    plugin<KoverGradlePlugin>()
    plugin<DokkaPlugin>()
    plugin<com.vanniktech.maven.publish.MavenPublishBasePlugin>()
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

  mavenPublishing {
    coordinates(group.toString(), project.name, version.toString())

    pom {
      name.set(project.name)
      description.set("Wrapper of Digipost API")
      inceptionYear.set("2025")
      url.set("https://github.com/Solibo-AS/solibo-digipost")
      licenses {
        license {
          name.set("The MIT License (MIT)")
          url.set("https://mit-license.org/")
          distribution.set("https://mit-license.org/")
        }
      }
      developers {
        developer {
          id.set("mikand13")
          name.set("Anders Enger Mikkelsen")
          url.set("https://github.com/mikand13")
        }
      }
      scm {
        url.set("https://github.com/Solibo-AS/solibo-digipost")
        connection.set("org-88184710@github.com:Solibo-AS/solibo-digipost.git")
        developerConnection.set("org-88184710@github.com:Solibo-AS/solibo-digipost.git")
      }
    }

    configure(KotlinJvm(
      javadocJar = JavadocJar.Dokka("dokkaHtml"),
      sourcesJar = true,
    ))

    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
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
}
