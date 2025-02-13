package pers.ketikai.broadcast.spigot.channel

import com.google.gson.Gson
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import pers.ketikai.broadcast.protocol.BroadcastLogHandler
import pers.ketikai.broadcast.protocol.BroadcastLogProtocol
import pers.ketikai.broadcast.protocol.BroadcastSender
import pers.ketikai.broadcast.spigot.Broadcast
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

class BroadcastLogChannel(private val plugin: Broadcast, private val channel: String): PluginMessageListener, BroadcastLogHandler {

    private val gson by lazy {
        Gson()
    }

    init {
        plugin.server.messenger.apply {
            registerOutgoingPluginChannel(plugin, channel)
            registerIncomingPluginChannel(plugin, channel, this@BroadcastLogChannel)
        }
    }

    override fun onPluginMessageReceived(channel: String, player: Player?, data: ByteArray) {
        channel != this.channel && return
        val broadcastLogProtocol = InputStreamReader(ByteArrayInputStream(data)).use {
            gson.fromJson(it, BroadcastLogProtocol::class.java)
        }
        receive(broadcastLogProtocol)
    }

    override fun send(sender: BroadcastSender, protocol: BroadcastLogProtocol) {
        // Nothing to do
    }

    override fun receive(protocol: BroadcastLogProtocol) {
        val message = protocol.message
        protocol.level.log(message)
    }
}