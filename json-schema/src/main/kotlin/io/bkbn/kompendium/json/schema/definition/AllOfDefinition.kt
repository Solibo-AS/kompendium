package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class AllOfDefinition(
  val allOf: Set<JsonSchema>,
  override val deprecated: Boolean? = null,
  override val description: String? = null,
) : JsonSchema
