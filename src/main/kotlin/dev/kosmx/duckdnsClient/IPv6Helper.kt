package dev.kosmx.duckdnsClient

import java.net.DatagramSocket
import java.net.Inet6Address
import java.net.UnknownHostException

/**
 * Test IPv6 access (does never cache)
 */
fun main() {
    getInet6IAAddress(true)
}

/**
 * uses *magic*
 *
 * Try to connect to kosmx.dev (can be changed to any server hosted on ipv6)
 * Check the local address in the connection -> it will be our public IPv6
 *
 * @return Local public IPv6 address
 */
fun getInet6IAAddress(verbose: Boolean): Inet6Address? {
    try {
        val probeServer: Inet6Address? =
            Inet6Address.getAllByName("kosmx.dev").firstNotNullOfOrNull { it as? Inet6Address }

        val ipv6: Inet6Address? = probeServer?.let {
            DatagramSocket().run {
                connect(probeServer, 443)
                localAddress.also { disconnect() }
            } as? Inet6Address?
        }


        if (verbose) {
            println("Local IPv6 is ${ipv6?.hostAddress}")
        }
        return ipv6
    } catch (e: UnknownHostException) {
        if (verbose) {
            println("No IPv6 detected")
        }
    }
    return null
}