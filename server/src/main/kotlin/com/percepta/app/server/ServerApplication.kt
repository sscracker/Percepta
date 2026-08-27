package com.percepta.app.server

import com.percepta.app.detection.createOnnxSession
import com.percepta.app.detection.decodeDetections
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receiveStream
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import javax.imageio.ImageIO

fun main() {
    val modelBytes = object {}.javaClass.getResourceAsStream("/yolov8n.onnx")
        ?.readBytes() ?: error("yolov8n.onnx not found in server resources")
    val session = createOnnxSession(modelBytes)

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() }
        routing {
            get("/health") {
                call.respondText("Percepta server OK")
            }
            post("/api/detect") {
                val image = ImageIO.read(call.receiveStream())
                    ?: return@post call.respondText("invalid image")
                val pre = preprocessServer(image)
                val output = session.run(pre.tensor)
                val detections = decodeDetections(output, pre.transform)
                call.respond(detections)
            }
        }
    }.start(wait = true)
}
