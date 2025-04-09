package io.bkbn.kompendium.oas.security

import kotlinx.serialization.Serializable

@Serializable
data class BearerAuth(
  val bearerFormat: String? = null,
  val type: String = "http",
  val scheme: String = "bearer",
) : SecuritySchema
