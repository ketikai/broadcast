package pers.ketikai.broadcast.test

import org.junit.jupiter.api.Test
import pers.ketikai.broadcast.common.Logger
import pers.ketikai.broadcast.generated.ProtocolProperties
import pers.ketikai.broadcast.protocol.BroadcastCommand
import pers.ketikai.broadcast.protocol.BroadcastConfiguration
import pers.ketikai.broadcast.protocol.BroadcastJobContainer
import pers.ketikai.broadcast.protocol.BroadcastProtocol
import pers.ketikai.broadcast.protocol.BroadcastProtocolHandler
import pers.ketikai.broadcast.protocol.BroadcastSender

open class CommandTest {

    private val command by lazy {
        val name = ProtocolProperties.PROJECT_COMMAND_MAIN
        val aliases = setOf(ProtocolProperties.PROJECT_COMMAND_ALIAS)
        val configuration = TestBroadcastConfiguration()
        val handler = TestBroadcastProtocolHandler()
        val jobs = TestBroadcastJobContainer()
        return@lazy BroadcastCommand(name, aliases, configuration, handler, jobs)
    }

    @Test
    fun test() {
        execute("broadcast reload")
        execute("brc commit now all tip 666")
        execute("brc commit tomorrow test notice activity")
        execute("brc commit night bukkit script fuck_boss")
        execute("brc job list")
        execute("brc job remove 123")
        execute("brc job list")
        execute("brc job clear")
        execute("brc job list")
    }

    private fun execute(request: String) {
        val arguments = request.split(" ")
        val name = arguments[0]
        val sender = TestBroadcastSender()
        command.execute(mutableMapOf(), sender, name, *arguments.subList(1, arguments.size).toTypedArray())
    }

    class TestBroadcastConfiguration: BroadcastConfiguration {
        override fun reloadConfig() {
            Logger.info("reloadConfig")
        }
    }

    class TestBroadcastProtocolHandler: BroadcastProtocolHandler {
        override fun send(sender: BroadcastSender, protocol: BroadcastProtocol) {
            Logger.info("send $protocol")
        }

        override fun receive(protocol: BroadcastProtocol) {
            Logger.info("send $protocol")
        }
    }

    class TestBroadcastJobContainer: BroadcastJobContainer {
        override val jobs: MutableMap<String, String> = mutableMapOf(
            "123" to "test",
            "1231" to "test1",
        )

        override fun remove(id: String): Boolean {
            Logger.info("remove $id")
            val contains = jobs.contains(id)
            if (contains) {
                jobs.remove(id)
            }
            return contains
        }

        override fun clear() {
            Logger.info("clear")
            jobs.clear()
        }
    }

    class TestBroadcastSender: BroadcastSender {
        override val logger: Logger
            get() = Logger.logger
        override val name: String
            get() = this::class.java.simpleName
        override val isAdmin: Boolean
            get() = true
        override val isPlayer: Boolean
            get() = false

        override fun hasPermission(permission: String): Boolean {
            return true
        }
    }
}