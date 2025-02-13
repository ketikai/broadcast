package pers.ketikai.broadcast.protocol

import java.io.Serializable

data class BroadcastProtocol(
    val crontab: String,
    val target: String,
    val action: BroadcastAction,
    val arguments: String
): Serializable {

    companion object {
        private const val serialVersionUID: Long = 4843451769650504639L

        @JvmStatic
        fun builder(): BroadcastProtocolBuilder {
            return ProtocolBuilder()
        }
    }

    private class ProtocolBuilder: BroadcastProtocolBuilder {

        override var crontab: String = ""
            private set
        override var target: String = ""
            private set
        override var action: BroadcastAction = BroadcastAction.UNKNOWN
            private set
        override var arguments: String = ""
            private set

        override fun by(original: BroadcastProtocol): BroadcastProtocolBuilder {
            original.apply {
                crontab(crontab)
                target(target)
                action(action)
                arguments(arguments)
            }
            return this
        }

        override fun crontab(crontab: String) = apply { this.crontab = crontab }
        override fun target(target: String) = apply { this.target = target }
        override fun action(action: BroadcastAction) = apply { this.action = action }
        override fun arguments(arguments: String) = apply { this.arguments = arguments }

        override fun build(): BroadcastProtocol {
            return BroadcastProtocol(crontab, target, action, arguments)
        }
    }
}
