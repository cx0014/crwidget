package com.cx0014.crwidget

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Converts [LocalTime] objects into [String] objects and vice versa
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalTime::class)
object TimeSerializer : KSerializer<LocalTime> {

    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        return LocalTime.parse(decoder.decodeString(), formatter)
    }
}