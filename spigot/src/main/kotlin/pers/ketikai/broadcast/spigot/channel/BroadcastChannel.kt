package pers.ketikai.broadcast.spigot.channel

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import pers.ketikai.broadcast.common.Logger
import pers.ketikai.broadcast.protocol.BroadcastAction
import pers.ketikai.broadcast.protocol.BroadcastEvent
import pers.ketikai.broadcast.protocol.BroadcastProtocol
import pers.ketikai.broadcast.protocol.BroadcastProtocolHandler
import pers.ketikai.broadcast.protocol.BroadcastSender
import pers.ketikai.broadcast.protocol.BroadcastState
import pers.ketikai.broadcast.spigot.Broadcast
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

class BroadcastChannel(private val plugin: Broadcast, private val channel: String): PluginMessageListener, BroadcastProtocolHandler {

    private val gson by lazy {
        Gson()
    }

    init {
        plugin.server.messenger.apply {
            registerOutgoingPluginChannel(plugin, channel)
            registerIncomingPluginChannel(plugin, channel, this@BroadcastChannel)
        }
    }

    override fun onPluginMessageReceived(channel: String, player: Player?, data: ByteArray) {
        channel != this.channel && return
        val broadcastProtocol = InputStreamReader(ByteArrayInputStream(data)).use {
            gson.fromJson(it, BroadcastProtocol::class.java)
        }

        receive(broadcastProtocol)
    }

    override fun send(sender: BroadcastSender, protocol: BroadcastProtocol) {
        BroadcastEvent(protocol, BroadcastState.SEND).apply {
            fire() || return
            editor.build().apply {
                Bukkit.getServer().sendPluginMessage(plugin, channel, gson.toJson(this).toByteArray())
            }
        }
    }

    override fun receive(protocol: BroadcastProtocol) {
        BroadcastEvent(protocol, BroadcastState.RECEIVE).apply {
            fire() || return
            editor.build().apply {
                when (action) {
                    BroadcastAction.UNKNOWN -> Logger.error("Unknown broadcast action.")
                    BroadcastAction.TIP, BroadcastAction.NOTICE -> {
                        val count = Bukkit.broadcastMessage(arguments)
                        Logger.info("Sent broadcast to $count players.")
                    }
                    BroadcastAction.SCRIPT -> {}
                    else -> {}
                }
            }
        }
    }
}