package dev.kosmx.duckdnsClient

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.net.URLEncoder


@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    val parser = ArgParser("DuckDNS utility")
    val verbose: Boolean by parser.option(ArgType.Boolean, "verbose", "v").default(false)
    parser.parse(args)

    val ipv6 by lazy{ getInet6IAAddress(verbose) }

    val dnsList: Map<String, DdnsEntry> = File("ddns.json").inputStream().use { Json.decodeFromStream(it) }


    dnsList.forEach { (name, entry) ->
        //https://www.duckdns.org/update?domains={YOURVALUE}&token={YOURVALUE}[&ip={YOURVALUE}][&ipv6={YOURVALUE}][&verbose=true][&clear=true]
        val urlBuilder =
            StringBuilder("https://www.duckdns.org/update?domains=$name&token=${entry.token.urlEncode()}&verbose=${verbose}")


        if (entry.protocol.ipv6) {

            if (ipv6 != null) {
                urlBuilder.append("&ipv6=${ipv6!!.hostAddress.urlEncode()}")
            } else {
                if (!entry.failHard) {
                    error("Can not bind IPv6 to $name")
                } else {
                    return@forEach
                }
            }
            val res = Jsoup.connect(urlBuilder.toString()).ignoreHttpErrors(!entry.failHard).method(Connection.Method.GET).execute()
            println(res.body())
        }
        if (entry.protocol.ipv4) {
            val res = Jsoup.connect("https://www.duckdns.org/update?domains=$name&token=${entry.token.urlEncode()}&verbose=${verbose}").ignoreHttpErrors(!entry.failHard).method(Connection.Method.GET).execute()
            println(res.body())
        }
    }
}

private fun String.urlEncode(): String = URLEncoder.encode(this, Charsets.UTF_8.name())

