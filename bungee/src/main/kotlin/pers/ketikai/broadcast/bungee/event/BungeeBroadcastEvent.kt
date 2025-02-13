package pers.ketikai.broadcast.bungee.event

import net.md_5.bungee.api.plugin.Cancellable
import net.md_5.bungee.api.plugin.Event
import pers.ketikai.broadcast.protocol.BroadcastEvent

class BungeeBroadcastEvent(private val broadcastEvent: BroadcastEvent): Event(), Cancellable {

    override fun isCancelled(): Boolean {
        return broadcastEvent.isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        broadcastEvent.isCancelled = cancel
    }
}