package pers.ketikai.broadcast.protocol

interface BroadcastProtocolBuilder {

    val crontab: String
    val target: String
    val action: BroadcastAction
    val arguments: String

    fun by(original: BroadcastProtocol): BroadcastProtocolBuilder
    fun crontab(crontab: String): BroadcastProtocolBuilder
    fun target(target: String): BroadcastProtocolBuilder
    fun action(action: BroadcastAction): BroadcastProtocolBuilder
    fun arguments(arguments: String): BroadcastProtocolBuilder
    fun build(): BroadcastProtocol
}