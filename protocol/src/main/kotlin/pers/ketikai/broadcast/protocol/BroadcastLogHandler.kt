package pers.ketikai.broadcast.protocol

interface BroadcastLogHandler {

    fun send(sender: BroadcastSender, protocol: BroadcastLogProtocol)

    fun receive(protocol: BroadcastLogProtocol)
}