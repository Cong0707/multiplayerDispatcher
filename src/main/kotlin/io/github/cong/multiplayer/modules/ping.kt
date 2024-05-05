package io.github.cong.multiplayer.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.ping(){
    routing {
        get("/"){
            call.respond(HttpStatusCode.OK, "Multiplayer service dispatcher")
        }
    }
}