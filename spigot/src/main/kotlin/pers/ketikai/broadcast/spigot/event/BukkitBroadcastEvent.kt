package pers.ketikai.broadcast.spigot.event

import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import pers.ketikai.broadcast.protocol.BroadcastEvent
import pers.ketikai.broadcast.protocol.BroadcastProtocol

class BukkitBroadcastEvent(protocol: BroadcastProtocol, state: BroadcastState): Event(false), BroadcastEvent, Cancellable {

    companion object {

        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }

    override val editor = BroadcastProtocolEditor.of(protocol, state)

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    private var cancelled = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun fire(): Boolean {
        Bukkit.getPluginManager().callEvent(this)
        return !isCancelled
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BukkitBroadcastEvent

        if (cancelled != other.cancelled) return false
        if (editor != other.editor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cancelled.hashCode()
        result = 31 * result + editor.hashCode()
        return result
    }
}
