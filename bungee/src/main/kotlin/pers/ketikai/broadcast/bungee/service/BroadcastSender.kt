package pers.ketikai.broadcast.bungee.service

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import pers.ketikai.broadcast.common.Logger
import pers.ketikai.broadcast.protocol.BroadcastSender

class BroadcastSender(private val sender: CommandSender = ProxyServer.getInstance().console):
    BroadcastSender {
    override val logger: Logger = Logger(sender)
    override val name: String
        get() = sender.name
    override val isAdmin: Boolean
        get() = sender.groups.contains("admin")
    override val isPlayer: Boolean = sender is ProxiedPlayer

    override fun hasPermission(permission: String): Boolean {
        return sender.hasPermission(permission)
    }
}