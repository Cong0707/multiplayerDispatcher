package io.github.cong.multiplayer.modules

import arc.util.Log
import arc.util.Time
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mindustry.net.Host
import mindustry.net.NetworkIO
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.time.Duration

@Serializable
data class Server(val address: String, val port: Int)

val servers = mutableListOf<Server>()

fun Application.servers() {
    install(WebSockets){
        pingPeriod = Duration.ofSeconds(5)
        timeout = Duration.ofSeconds(10)
        maxFrameSize = Long.MAX_VALUE
        masking = true
    }

    routing {
        get("/client/servers"){
            call.respondText(
                Json.encodeToString(servers)
            )
        }

        webSocket("/server/connect"){
            try {
                send("Welcome to multiplayer service.")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()

                    try {
                        @Serializable
                        data class RegisterPacket(val port: Int)

                        val newServer = Server(call.request.origin.remoteAddress, Json.decodeFromString<RegisterPacket>(receivedText).port)
                        pingHostImpl(newServer.address, newServer.port)
                        servers.add(newServer)
                        send("$newServer added")
                    } catch (e: Exception){
                        Log.err(e)
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                servers.removeAll { it.address == call.request.origin.remoteAddress }
            }
        }
    }

}

private fun pingHostImpl(address: String, port: Int): Host {
    DatagramSocket().use { socket ->
        val time = Time.millis()
        socket.send(
            DatagramPacket(
                byteArrayOf(-2, 1),
                2,
                InetAddress.getByName(address),
                port
            )
        )
        socket.soTimeout = 2000

        val packet = DatagramPacket(ByteArray(512), 512)
        socket.receive(packet)

        val buffer = ByteBuffer.wrap(packet.data)
        val host = NetworkIO.readServerData(
            Time.timeSinceMillis(time).toInt(),
            packet.address.hostAddress,
            buffer
        )
        host.port = port
        return host
    }
}