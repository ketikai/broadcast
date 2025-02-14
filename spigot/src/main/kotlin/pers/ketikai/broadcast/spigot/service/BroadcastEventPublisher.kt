package pers.ketikai.broadcast.spigot.service

import org.bukkit.Bukkit
import pers.ketikai.broadcast.protocol.BroadcastEvent
import pers.ketikai.broadcast.protocol.BroadcastEventPublisher
import pers.ketikai.broadcast.spigot.event.BukkitBroadcastEvent

class BroadcastEventPublisher: BroadcastEventPublisher {
    override fun publish(broadcastEvent: BroadcastEvent) {
        val pluginManager = Bukkit.getPluginManager()
        val bukkitBroadcastEvent = BukkitBroadcastEvent(broadcastEvent)
        pluginManager.callEvent(bukkitBroadcastEvent)
    }
}