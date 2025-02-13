package pers.ketikai.broadcast.bungee.service

import net.md_5.bungee.api.ProxyServer
import pers.ketikai.broadcast.bungee.event.BungeeBroadcastEvent
import pers.ketikai.broadcast.protocol.BroadcastEvent
import pers.ketikai.broadcast.protocol.BroadcastEventPublisher

class BroadcastEventPublisher: BroadcastEventPublisher {
    override fun publish(broadcastEvent: BroadcastEvent) {
        val proxy = ProxyServer.getInstance()
        val pluginManager = proxy.pluginManager
        val bungeeBroadcastEvent = BungeeBroadcastEvent(broadcastEvent)
        pluginManager.callEvent(bungeeBroadcastEvent)
    }
}