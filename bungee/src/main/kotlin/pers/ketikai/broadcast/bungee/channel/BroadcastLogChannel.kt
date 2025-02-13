package pers.ketikai.broadcast.bungee.channel

import com.google.gson.Gson
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import pers.ketikai.broadcast.bungee.Broadcast
import pers.ketikai.broadcast.protocol.BroadcastLogHandler
import pers.ketikai.broadcast.protocol.BroadcastLogProtocol
import pers.ketikai.broadcast.protocol.BroadcastSender

class BroadcastLogChannel(private val plugin: Broadcast, private val channel: String): Listener, BroadcastLogHandler {

    private val gson by lazy {
        Gson()
    }

    init {
        plugin.proxy.registerChannel(channel)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPluginMessageReceived(event: PluginMessageEvent) {
        event.tag != channel && return
//        receive(?)
        event.isCancelled = true
    }

    override fun send(sender: BroadcastSender, protocol: BroadcastLogProtocol) {
        val proxy = plugin.proxy
        val name = sender.name
        if (sender.isPlayer) {
            val player = proxy.getPlayer(name)
            player?.sendMessage(TextComponent(protocol.message))
        } else {
            val server = proxy.getServerInfo(name)
            val json = gson.toJson(protocol)
            server.sendData(channel, json.toByteArray(), false)
        }
    }

    override fun receive(protocol: BroadcastLogProtocol) {
        // Nothing to do
    }
}