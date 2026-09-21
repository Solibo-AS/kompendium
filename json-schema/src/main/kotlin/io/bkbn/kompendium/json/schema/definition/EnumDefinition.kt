package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

/**
 * Enum constants always serialize as their string name, so the definition carries an explicit
 * `type`. A bare `enum` is valid JSON Schema, but leaves the type undetermined, which makes
 * Swagger UI render the field as `any` and gives code generators nothing to map to a string enum.
 */
@Serializable
data class EnumDefinition(
  val enum: Set<String>,
  val type: String = "string",
  override val deprecated: Boolean? = null,
  override val description: String? = null,
) : JsonSchema
