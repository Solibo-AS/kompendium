package io.bkbn.kompendium.json.schema.definition

import kotlinx.serialization.Serializable

@Serializable
data class Discriminator(
  val propertyName: String,
  val mapping: Map<String, String>? = null,
)
