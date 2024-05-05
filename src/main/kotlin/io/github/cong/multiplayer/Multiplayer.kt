package io.github.cong.multiplayer

import io.github.cong.multiplayer.modules.servers
import io.github.cong.multiplayer.modules.ping
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {

    embeddedServer(CIO, port = 8667, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    ping()
    servers()
}
