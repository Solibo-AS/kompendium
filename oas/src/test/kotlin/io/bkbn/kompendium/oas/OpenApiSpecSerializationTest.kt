package io.bkbn.kompendium.oas

import io.bkbn.kompendium.json.schema.definition.AllOfDefinition
import io.bkbn.kompendium.json.schema.definition.Discriminator
import io.bkbn.kompendium.json.schema.definition.OneOfDefinition
import io.bkbn.kompendium.json.schema.definition.ReferenceDefinition
import io.bkbn.kompendium.json.schema.definition.TypeDefinition
import io.bkbn.kompendium.oas.info.Info
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.json.Json

class OpenApiSpecSerializationTest : DescribeSpec({
  val json = Json {
    encodeDefaults = true
    explicitNulls = false
    prettyPrint = true
  }

  describe("OpenApiSpec") {
    it("serializes with AllOfDefinition in components") {
      val spec = OpenApiSpec(
        info = Info(
          title = "Test API",
          version = "1.0.0"
        )
      ).apply {
        components.schemas["Resident"] = TypeDefinition(
          type = "object",
          properties = mapOf(
            "id" to TypeDefinition.LONG,
            "country" to TypeDefinition.STRING
          ),
          required = setOf("id", "country")
        )
        components.schemas["PersonResident"] = AllOfDefinition(
          allOf = setOf(
            ReferenceDefinition("#/components/schemas/Resident"),
            TypeDefinition(
              type = "object",
              properties = mapOf("name" to TypeDefinition.STRING),
              required = setOf("name")
            )
          )
        )
      }

      val result = json.encodeToString(OpenApiSpec.serializer(), spec)

      result shouldEqualJson """
        {
          "openapi": "3.1.0",
          "jsonSchemaDialect": "https://json-schema.org/draft/2020-12/schema",
          "info": {
            "title": "Test API",
            "version": "1.0.0"
          },
          "servers": [],
          "paths": {},
          "webhooks": {},
          "components": {
            "schemas": {
              "Resident": {
                "type": "object",
                "properties": {
                  "id": {
                    "type": "number",
                    "format": "int64"
                  },
                  "country": {
                    "type": "string"
                  }
                },
                "required": [
                  "id",
                  "country"
                ]
              },
              "PersonResident": {
                "allOf": [
                  {
                    "${'$'}ref": "#/components/schemas/Resident"
                  },
                  {
                    "type": "object",
                    "properties": {
                      "name": {
                        "type": "string"
                      }
                    },
                    "required": [
                      "name"
                    ]
                  }
                ]
              }
            },
            "securitySchemes": {}
          },
          "security": [],
          "tags": []
        }
      """.trimIndent()
    }

    it("serializes with OneOfDefinition and discriminator in components") {
      val spec = OpenApiSpec(
        info = Info(
          title = "Test API",
          version = "1.0.0"
        )
      ).apply {
        components.schemas["ResidentPolymorphic"] = OneOfDefinition(
          oneOf = setOf(
            ReferenceDefinition("#/components/schemas/PersonResident"),
            ReferenceDefinition("#/components/schemas/OrganizationResident")
          ),
          discriminator = Discriminator(
            propertyName = "type",
            mapping = mapOf(
              "person" to "#/components/schemas/PersonResident",
              "org" to "#/components/schemas/OrganizationResident"
            )
          )
        )
      }

      val result = json.encodeToString(OpenApiSpec.serializer(), spec)

      result shouldEqualJson """
        {
          "openapi": "3.1.0",
          "jsonSchemaDialect": "https://json-schema.org/draft/2020-12/schema",
          "info": {
            "title": "Test API",
            "version": "1.0.0"
          },
          "servers": [],
          "paths": {},
          "webhooks": {},
          "components": {
            "schemas": {
              "ResidentPolymorphic": {
                "oneOf": [
                  {
                    "${'$'}ref": "#/components/schemas/PersonResident"
                  },
                  {
                    "${'$'}ref": "#/components/schemas/OrganizationResident"
                  }
                ],
                "discriminator": {
                  "propertyName": "type",
                  "mapping": {
                    "person": "#/components/schemas/PersonResident",
                    "org": "#/components/schemas/OrganizationResident"
                  }
                }
              }
            },
            "securitySchemes": {}
          },
          "security": [],
          "tags": []
        }
      """.trimIndent()
    }
  }
})
