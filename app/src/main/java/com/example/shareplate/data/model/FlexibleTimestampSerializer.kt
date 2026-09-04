package com.example.shareplate.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.OffsetDateTime


object FlexibleTimestampSerializer : KSerializer<Long> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "FlexibleTimestamp", PrimitiveKind.STRING
    )


    override fun deserialize(
        decoder: Decoder
    ): Long {

        val value = decoder.decodeString()


        // If database somehow returns a numeric timestamp as text
        value.toLongOrNull()?.let {

            return if (it < 100_000_000_000L) {

                it * 1000

            } else {

                it
            }
        }


        // Supabase timestamptz
        return try {

            Instant.parse(value).toEpochMilli()

        } catch (e: Exception) {

            OffsetDateTime.parse(value).toInstant().toEpochMilli()
        }
    }


    override fun serialize(
        encoder: Encoder, value: Long
    ) {

        val milliseconds =

            if (value < 100_000_000_000L) {

                value * 1000

            } else {

                value
            }


        encoder.encodeString(

            Instant.ofEpochMilli(milliseconds).toString()
        )
    }
}