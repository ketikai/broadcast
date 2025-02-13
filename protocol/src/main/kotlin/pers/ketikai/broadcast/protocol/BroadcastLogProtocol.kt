package pers.ketikai.broadcast.protocol

import pers.ketikai.broadcast.common.LogLevel
import java.io.Serializable

data class BroadcastLogProtocol(
    val level: LogLevel,
    val message: String
): Serializable {
    companion object {
        private const val serialVersionUID: Long = -6877253755513156271L
    }

    override fun toString(): String {
        return "BroadcastLogProtocol(level=$level, message='$message')"
    }
}
