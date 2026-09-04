package com.example.shareplate.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.time.Instant

object FlexibleStringSerializer : KSerializer<String> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            "FlexibleString",
            PrimitiveKind.STRING
        )

    override fun deserialize(
        decoder: Decoder
    ): String {

        if (decoder is JsonDecoder) {

            val primitive =
                decoder
                    .decodeJsonElement()
                    .jsonPrimitive

            // Normal Supabase timestamptz string
            if (primitive.isString) {
                return primitive.content
            }

            // Old numeric timestamp
            val number =
                primitive.longOrNull
                    ?: return primitive.content

            val milliseconds =
                if (number < 100_000_000_000L) {
                    number * 1000L
                } else {
                    number
                }

            // Convert Long timestamp to ISO String
            return Instant
                .ofEpochMilli(milliseconds)
                .toString()
        }

        return decoder.decodeString()
    }

    override fun serialize(
        encoder: Encoder,
        value: String
    ) {
        encoder.encodeString(value)
    }
}