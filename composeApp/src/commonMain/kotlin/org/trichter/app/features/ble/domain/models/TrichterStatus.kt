package org.trichter.app.features.ble.domain.models

data class TrichterState(
    val connection: Connection = Connection.Disconnected,
    val status: SessionStatus = SessionStatus.UNKNOWN,
    val lastResultMeta: ResultMeta? = null,
    val lastImage: ByteArray? = null
)

enum class Connection { Disconnected, Connecting, Connected }
enum class SessionStatus { IDLE, WAITING, RUNNING, COMPLETE, ERROR, UNKNOWN }

data class ResultMeta(
    val durationMs: Long,
    val rateLpm: Float,
    val volumeL: Float,
    val hasImage: Boolean,
    val imageSize: Int
)

fun ResultMeta.toNewRun(): NewRunDto =
    NewRunDto(
        durationS = durationMs / 1000f,
        rateLpm = rateLpm,
        volumeL = volumeL
    )
