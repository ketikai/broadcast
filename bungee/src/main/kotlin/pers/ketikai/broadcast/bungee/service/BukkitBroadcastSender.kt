package pers.ketikai.broadcast.bungee.service

import net.md_5.bungee.api.connection.Server
import pers.ketikai.broadcast.bungee.Broadcast
import pers.ketikai.broadcast.common.Logger
import pers.ketikai.broadcast.protocol.BroadcastSender

internal class BukkitBroadcastSender(server: Server, private val plugin: Broadcast): BroadcastSender {
    override val logger: Logger by lazy {
        BukkitLogger(server, plugin)
    }
    override val name: String = server.info.name
    override val isAdmin: Boolean = true
    override val isPlayer: Boolean = false

    override fun hasPermission(permission: String): Boolean {
        return true
    }
}