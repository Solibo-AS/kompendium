package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

@Serializable(with = OneOfDefinitionSerializer::class)
data class OneOfDefinition(
  val oneOf: Set<JsonSchema>,
  override val deprecated: Boolean? = null,
  override val description: String? = null,
) : JsonSchema {
  constructor(vararg types: JsonSchema) : this(types.toSet())
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = OneOfDefinition::class)
object OneOfDefinitionSerializer : KSerializer<OneOfDefinition> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OneOfDefinition")

  override fun serialize(encoder: Encoder, value: OneOfDefinition) {
    val jsonEncoder = encoder as? JsonEncoder ?: error("This class can only be serialized to JSON")

    val schemas = value.oneOf
    val isNullable = schemas.any { it is NullableDefinition }
    val nonNullSchemas = schemas.filterNot { it is NullableDefinition }

    val jsonObject = when {
      isNullable && nonNullSchemas.size == 1 -> {
        val baseSchema = nonNullSchemas.first()
        val baseJson = jsonEncoder.json.encodeToJsonElement(JsonSchema.serializer(), baseSchema).jsonObject
        JsonObject(
          buildMap {
            putAll(baseJson)
            put("nullable", JsonPrimitive(true))
            value.description?.let { put("description", JsonPrimitive(it)) }
            value.deprecated?.let { put("deprecated", JsonPrimitive(it)) }
          }
        )
      }

      else -> {
        val oneOfJson = schemas.map {
          jsonEncoder.json.encodeToJsonElement(
            serializer = JsonSchema.serializer(),
            value = it,
          )
        }
        JsonObject(
          buildMap {
            put("oneOf", JsonArray(oneOfJson))
            value.description?.let { put("description", JsonPrimitive(it)) }
            value.deprecated?.let { put("deprecated", JsonPrimitive(it)) }
          }
        )
      }
    }

    jsonEncoder.encodeJsonElement(jsonObject)
  }
}
