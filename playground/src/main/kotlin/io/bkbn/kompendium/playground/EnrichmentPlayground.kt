package io.bkbn.kompendium.playground

import io.bkbn.kompendium.core.metadata.PostInfo
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.bkbn.kompendium.core.routes.redoc
import io.bkbn.kompendium.core.routes.swagger
import io.bkbn.kompendium.enrichment.BooleanEnrichment
import io.bkbn.kompendium.enrichment.NumberEnrichment
import io.bkbn.kompendium.enrichment.ObjectEnrichment
import io.bkbn.kompendium.enrichment.StringEnrichment
import io.bkbn.kompendium.json.schema.KotlinXSchemaConfigurator
import io.bkbn.kompendium.oas.serialization.KompendiumSerializersModule
import io.bkbn.kompendium.playground.util.ExampleRequest
import io.bkbn.kompendium.playground.util.ExampleResponse
import io.bkbn.kompendium.playground.util.InnerRequest
import io.bkbn.kompendium.playground.util.Util.baseSpec
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
  embeddedServer(
    CIO,
    port = 8081,
    module = Application::mainModule
  ).start(wait = true)
}

private fun Application.mainModule() {
  install(ContentNegotiation) {
    json(
      Json {
        serializersModule = KompendiumSerializersModule.module
        encodeDefaults = true
        explicitNulls = false
      }
    )
  }
  install(NotarizedApplication()) {
    spec = { baseSpec }
    // Adds support for @Transient and @SerialName
    // If you are not using them this is not required.
    schemaConfigurator = KotlinXSchemaConfigurator()
  }
  routing {
    swagger(pageTitle = "Simple API Docs")
    redoc(pageTitle = "Simple API Docs")
    enrichedDocumentation()
    post {
      call.respond(HttpStatusCode.OK, ExampleResponse(false))
    }
  }
}

private val testEnrichment = ObjectEnrichment("testerino") {
  ExampleRequest::thingA {
    StringEnrichment("thingA") {
      description = "This is a thing"
    }
  }
  ExampleRequest::thingB {
    NumberEnrichment("thingB") {
      description = "This is another thing"
    }
  }
  ExampleRequest::thingC {
    ObjectEnrichment<InnerRequest>("thingC") {
      deprecated = true
      description = "A good but old field"
      InnerRequest::d {
        NumberEnrichment("blahblah") {
          @Suppress("MagicNumber")
          exclusiveMinimum = 1.1
          @Suppress("MagicNumber")
          exclusiveMaximum = 10.0
          description = "THE BIG D"
        }
      }
    }
  }
}

private val testResponseEnrichment = ObjectEnrichment("testerino") {
  ExampleResponse::isReal {
    BooleanEnrichment("blah") {
      description = "Is this thing real or not?"
    }
  }
}

private fun Route.enrichedDocumentation() {
  install(NotarizedRoute()) {
    post = PostInfo.builder {
      summary("Do a thing")
      description("This is a thing")
      request {
        requestType(enrichment = testEnrichment)
        description("This is the request")
      }
      response {
        responseCode(HttpStatusCode.OK)
        responseType(enrichment = testResponseEnrichment)
        description("This is the response")
      }
    }
  }
}
