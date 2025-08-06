@file:OptIn(ExperimentalTime::class)

package io.bkbn.kompendium.playground.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class ExampleRequest(
  val thingA: String,
  val thingB: Int,
  val thingC: InnerRequest,
)

@Serializable
data class InnerRequest(
  val d: Float,
  val e: Boolean,
)

@Serializable
data class ExampleResponse(val isReal: Boolean)

@Serializable
data class CustomTypeResponse(
  val thing: String,
  @Serializable(with = KotlinTimeInstantSerializer::class)
  val timestamp: Instant
)

object KotlinTimeInstantSerializer : KSerializer<Instant> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("KotlinTimeInstant", PrimitiveKind.DOUBLE)

  override fun serialize(encoder: Encoder, value: Instant) {
    encoder.encodeDouble(value.toEpochMilliseconds().toDouble())
  }

  override fun deserialize(decoder: Decoder): Instant {
    return Instant.fromEpochMilliseconds(decoder.decodeDouble().toLong())
  }
}

@Serializable
data class ExceptionResponse(val message: String)
