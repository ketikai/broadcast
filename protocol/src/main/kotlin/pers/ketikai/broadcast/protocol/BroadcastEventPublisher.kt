package pers.ketikai.broadcast.protocol

interface BroadcastEventPublisher {

    fun publish(broadcastEvent: BroadcastEvent)
}