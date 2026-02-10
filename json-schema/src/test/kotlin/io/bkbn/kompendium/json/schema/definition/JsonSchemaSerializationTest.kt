package io.bkbn.kompendium.json.schema.definition

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.json.Json

class JsonSchemaSerializationTest : DescribeSpec({
  val json = Json {
    encodeDefaults = true
    explicitNulls = false
    prettyPrint = true
  }

  describe("AllOfDefinition") {
    it("serializes correctly") {
      val schema: JsonSchema = AllOfDefinition(
        allOf = setOf(
          ReferenceDefinition("#/components/schemas/Resident"),
          TypeDefinition(
            type = "object",
            properties = mapOf("name" to TypeDefinition.STRING),
            required = setOf("name")
          )
        )
      )
      val result = json.encodeToString(JsonSchema.serializer(), schema)
      result shouldEqualJson """
        {
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
      """.trimIndent()
    }
  }

  describe("OneOfDefinition with Discriminator") {
    it("serializes standard oneOf with discriminator") {
      val schema: JsonSchema = OneOfDefinition(
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
      val result = json.encodeToString(JsonSchema.serializer(), schema)
      result shouldEqualJson """
        {
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
      """.trimIndent()
    }

    it("serializes flattened nullable oneOf with discriminator") {
      val schema: JsonSchema = OneOfDefinition(
        oneOf = setOf(
          ReferenceDefinition("#/components/schemas/PersonResident"),
          NullableDefinition()
        ),
        discriminator = Discriminator(
          propertyName = "type"
        )
      )
      val result = json.encodeToString(JsonSchema.serializer(), schema)
      result shouldEqualJson """
        {
          "${'$'}ref": "#/components/schemas/PersonResident",
          "nullable": true,
          "discriminator": {
            "propertyName": "type"
          }
        }
      """.trimIndent()
    }
  }
})
