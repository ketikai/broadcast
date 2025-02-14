package pers.ketikai.broadcast.spigot.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import pers.ketikai.broadcast.protocol.BroadcastEvent

class BukkitBroadcastEvent(private val broadcastEvent: BroadcastEvent): Event(false), Cancellable {

    companion object {

        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    override fun isCancelled(): Boolean {
        return broadcastEvent.isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        broadcastEvent.isCancelled = cancel
    }
}
