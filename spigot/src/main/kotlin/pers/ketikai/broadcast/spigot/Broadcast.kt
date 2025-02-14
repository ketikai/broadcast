package pers.ketikai.broadcast.spigot

import org.bukkit.plugin.java.JavaPlugin
import pers.ketikai.broadcast.common.Banner
import pers.ketikai.broadcast.common.Logger
import pers.ketikai.broadcast.generated.ProtocolProperties
import pers.ketikai.broadcast.protocol.BroadcastJobContainer
import pers.ketikai.broadcast.protocol.BroadcastLogHandler
import pers.ketikai.broadcast.protocol.BroadcastProtocolHandler
import pers.ketikai.broadcast.spigot.channel.BroadcastChannel
import pers.ketikai.broadcast.spigot.channel.BroadcastLogChannel
import pers.ketikai.broadcast.spigot.command.BroadcastCommand

class Broadcast: JavaPlugin() {

    private var _handler: BroadcastProtocolHandler? = null
    val handler
        get() = _handler ?: throw IllegalStateException("handler is not initialized yet.")
    private var _logHandler: BroadcastLogHandler? = null
    val logHandler
        get() = _logHandler ?: throw IllegalStateException("Log handler is not initialized yet.")
    private var _jobs: BroadcastJobContainer? = pers.ketikai.broadcast.spigot.service.BroadcastJobContainer()
    val jobs
        get() = _jobs ?: throw IllegalStateException("Jobs is not initialized yet.")

    override fun onLoad() {
        Logger.debug("onLoad")
        Logger.info(*Banner.lines)
    }

    override fun onEnable() {
        Logger.debug("onEnable")
        registerListener(this)
        registerChannel(this)
        registerCommand(this)
    }

    override fun onDisable() {
        Logger.debug("onDisable")
    }

    private fun registerListener(plugin: Broadcast) {
        Logger.debug("registerListener")
        server.pluginManager.apply {
//            registerEvents(?, plugin)
        }
    }

    private fun registerChannel(plugin: Broadcast) {
        Logger.debug("registerChannel")
        server.messenger.apply {
            val broadcastChannel = BroadcastChannel(plugin, ProtocolProperties.PROJECT_CHANNEL)
            _handler = broadcastChannel
            val broadcastLogChannel = BroadcastLogChannel(plugin, ProtocolProperties.PROJECT_LOG_CHANNEL)
            _logHandler = broadcastLogChannel
        }
    }

    private fun registerCommand(plugin: Broadcast) {
        Logger.debug("registerCommand")
        getCommand(ProtocolProperties.PROJECT_COMMAND_MAIN).apply {
            val broadcastCommand = BroadcastCommand(
                ProtocolProperties.PROJECT_COMMAND_MAIN, setOf(ProtocolProperties.PROJECT_COMMAND_ALIAS),plugin)
            executor = broadcastCommand
        }
    }
}