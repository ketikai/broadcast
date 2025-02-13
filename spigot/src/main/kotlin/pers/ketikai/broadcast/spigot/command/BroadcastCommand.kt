package pers.ketikai.broadcast.spigot.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pers.ketikai.broadcast.spigot.Broadcast
import pers.ketikai.broadcast.spigot.service.BroadcastSender

@Suppress("CanBeParameter")
class BroadcastCommand(private val plugin: Broadcast): CommandExecutor {

    private val commander = Commander(plugin.handler)

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        request: String,
        args: Array<out String>
    ): Boolean {
        sender is Player && return false
        val broadcastCommandSender = BroadcastSender(sender)
        return commander.execute(broadcastCommandSender, args)
    }
}