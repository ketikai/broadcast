package pers.ketikai.broadcast.bungee.service

import net.md_5.bungee.api.connection.Server
import pers.ketikai.broadcast.bungee.Broadcast
import pers.ketikai.broadcast.common.LogLevel
import pers.ketikai.broadcast.common.Logger
import pers.ketikai.broadcast.protocol.BroadcastLogProtocol
import pers.ketikai.broadcast.protocol.BroadcastSender

internal class BukkitLogger(server: Server, private val plugin: Broadcast): Logger {

    private object FakeLogger: Logger {
        override fun println(vararg message: String) {
            // Nothing to do
        }
    }

    private class FakeSender(private val server: Server): BroadcastSender {
        override val logger: Logger = FakeLogger
        override val name: String
            get() = server.info.name
        override val isAdmin: Boolean = true
        override val isPlayer: Boolean = false

        override fun hasPermission(permission: String): Boolean {
            return true
        }
    }

    private val sender = FakeSender(server)

    override fun println(vararg message: String) {
        val broadcastLogProtocol = BroadcastLogProtocol(LogLevel.UNKNOWN, message.joinToString("\n"))
        plugin.logHandler.send(sender, broadcastLogProtocol)
    }
}