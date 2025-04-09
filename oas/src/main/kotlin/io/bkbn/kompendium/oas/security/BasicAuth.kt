package io.bkbn.kompendium.oas.security

import kotlinx.serialization.Serializable

@Serializable
class BasicAuth(
  val type: String = "http",
  val scheme: String = "basic",
) : SecuritySchema
