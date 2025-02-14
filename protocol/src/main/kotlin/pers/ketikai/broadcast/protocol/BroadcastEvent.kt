package pers.ketikai.broadcast.protocol

import pers.ketikai.broadcast.common.Services

@Suppress("MemberVisibilityCanBePrivate")
class BroadcastEvent(
    val original: BroadcastProtocol,
    val state: BroadcastState
) {
    private val publisher by lazy {
        Services.singleton(BroadcastEventPublisher::class.java, this::class.java.classLoader)
    }

    val editor: BroadcastProtocolBuilder by lazy {
        BroadcastProtocol.builder().by(original)
    }

    var isCancelled: Boolean = false
        set(value) {
            value || throw UnsupportedOperationException("BroadcastEvent does not support changing the status to Uncancelled.")
            field = value
        }

    fun cancel() {
        isCancelled = true
    }

    fun fire(): Boolean {
        publisher.publish(this)
        return !isCancelled
    }
}