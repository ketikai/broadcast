package pers.ketikai.broadcast.bungee.command

import net.md_5.bungee.api.CommandSender
import pers.ketikai.broadcast.bungee.Broadcast
import pers.ketikai.broadcast.bungee.service.BroadcastConfiguration
import pers.ketikai.broadcast.bungee.service.BroadcastSender
import pers.ketikai.broadcast.generated.ProtocolProperties
import pers.ketikai.broadcast.protocol.BroadcastCommand

@Suppress("CanBeParameter")
class BroadcastCommand(private val plugin: Broadcast): net.md_5.bungee.api.plugin.Command(
    ProtocolProperties.PROJECT_COMMAND_MAIN, null, ProtocolProperties.PROJECT_COMMAND_ALIAS
) {

    private val delegate = BroadcastCommand(name, aliases.toSet(), BroadcastConfiguration(plugin), plugin.handler)

    override fun execute(sender: CommandSender, vararg args: String) {
        val broadcastSender = BroadcastSender(sender)
        delegate.execute(mutableMapOf(), broadcastSender, name, *args)
    }
}