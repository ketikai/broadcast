package pers.ketikai.broadcast.spigot.service

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pers.ketikai.broadcast.common.Logger
import pers.ketikai.broadcast.protocol.BroadcastSender

class BroadcastSender(private val sender: CommandSender = Bukkit.getConsoleSender()):
    BroadcastSender {
    override val logger: Logger = Logger(sender)
    override val name: String
        get() = sender.name
    override val isAdmin: Boolean
        get() = sender.isOp
    override val isPlayer: Boolean = sender is Player

    override fun hasPermission(permission: String): Boolean {
        return sender.hasPermission(permission)
    }
}