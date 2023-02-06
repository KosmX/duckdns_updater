package dev.kosmx.duckdnsClient

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress

@Serializable
data class DdnsEntry(
    val token: String,
    val protocol: Protocol = Protocol.Both,
    val failHard: Boolean = true,
)

private val ipv4Resolver = {address: String -> Inet4Address.getByName(address)!!}
private val ipv6Resolver = {address: String -> Inet6Address.getByName(address)!!}

@Serializable(with = Protocol.Companion::class)
enum class Protocol(
        val ipv4: Boolean, val ipv6: Boolean,
        val resolvers: Array<(String) -> InetAddress>) {
    IPv4(true, false, arrayOf(ipv4Resolver)),
    IPv6(false, true, arrayOf(ipv6Resolver)),
    Both(true, true, arrayOf(ipv4Resolver, ipv6Resolver)),
    ;

    companion object : KSerializer<Protocol> {

        override fun deserialize(decoder: Decoder): Protocol {
            val string = decoder.decodeString()
            return when (string.lowercase()) {
                "ipv4" -> IPv4
                "ipv6" -> IPv6
                "*", "both" -> Both
                else -> error("Unexpected protocol: $string")
            }
        }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Protocol", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Protocol) {
            when (value) {
                IPv4, IPv6 -> value.toString()
                Both -> "*"
            }.let {
                encoder.encodeString(it)
            }
        }
    }
}
