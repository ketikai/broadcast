package pers.ketikai.broadcast.protocol

interface BroadcastProtocolHandler {

    fun send(sender: BroadcastSender, protocol: BroadcastProtocol)

    fun receive(protocol: BroadcastProtocol)
}