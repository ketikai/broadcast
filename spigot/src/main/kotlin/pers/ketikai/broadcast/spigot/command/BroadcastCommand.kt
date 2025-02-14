package pers.ketikai.broadcast.spigot.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pers.ketikai.broadcast.protocol.BroadcastCommand
import pers.ketikai.broadcast.spigot.Broadcast
import pers.ketikai.broadcast.spigot.service.BroadcastConfiguration
import pers.ketikai.broadcast.spigot.service.BroadcastSender

@Suppress("CanBeParameter")
class BroadcastCommand(
    private val name: String,
    private val aliases: Set<String>,
    private val plugin: Broadcast): CommandExecutor {

    private val delegate = BroadcastCommand(name, aliases.toSet(), BroadcastConfiguration(), plugin.handler, plugin.jobs)

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        request: String,
        vararg args: String
    ): Boolean {
        sender is Player && return false
        val broadcastCommandSender = BroadcastSender(sender)
        return delegate.execute(mutableMapOf(), broadcastCommandSender, request, *args)
    }
}