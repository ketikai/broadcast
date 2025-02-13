package pers.ketikai.broadcast.bungee

import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import pers.ketikai.broadcast.bungee.channel.BroadcastChannel
import pers.ketikai.broadcast.bungee.channel.BroadcastLogChannel
import pers.ketikai.broadcast.bungee.command.BroadcastCommand
import pers.ketikai.broadcast.bungee.data.Config
import pers.ketikai.broadcast.common.Banner
import pers.ketikai.broadcast.common.Bundled
import pers.ketikai.broadcast.common.Logger
import pers.ketikai.broadcast.generated.ProtocolProperties
import pers.ketikai.broadcast.protocol.BroadcastLogHandler
import pers.ketikai.broadcast.protocol.BroadcastProtocolHandler
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.relativeTo

@Suppress("MemberVisibilityCanBePrivate")
class Broadcast: Plugin() {

    companion object {
        const val NOTICES_DIR_PATH = "/notices/"
        const val CONFIG_FILE_PATH = "/config.yml"
    }

    private var _handler: BroadcastProtocolHandler? = null
    val handler
        get() = _handler ?: throw IllegalStateException("Handler is not initialized yet.")
    private var _logHandler: BroadcastLogHandler? = null
    val logHandler
        get() = _logHandler ?: throw IllegalStateException("Log handler is not initialized yet.")

    private var _config: Config? = null

    val config: Config
        get() {
            val configuration = _config ?: return loadConfig()
            return configuration
        }

    private fun loadConfig(): Config {
        Logger.debug("loadConfig")
        val configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration::class.java)
        val file = File(dataFolder, CONFIG_FILE_PATH)
        val configuration = if (file.exists()) configurationProvider.load(file) else configurationProvider.load("")
        val config = Config( configuration)
        _config = config
        if (config.debug) {
            Logger.enableDebug()
        } else {
            Logger.disableDebug()
        }
        return config
    }

    fun getNotice(name: String): List<String>? {
        val notice = Paths.get(NOTICES_DIR_PATH, name).relativeTo(dataFolder.toPath()).toFile()
        return if (notice.exists()) {
            notice.readLines()
        } else {
            null
        }
    }

    override fun onLoad() {
        Logger.debug("onLoad")
        Logger.info(*Banner.lines)
        Bundled.release(dataFolder)
        loadConfig()
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

    fun reloadConfig() {
        Logger.debug("reloadConfig")
        loadConfig()
    }

    private fun registerListener(plugin: Broadcast) {
        Logger.debug("registerListener")
        proxy.pluginManager.apply {
//            registerListener(plugin, ?)
        }
    }

    private fun registerChannel(plugin: Broadcast) {
        Logger.debug("registerChannel")
        proxy.pluginManager.apply {
            val broadcastChannel = BroadcastChannel(plugin, ProtocolProperties.PROJECT_CHANNEL)
            registerListener(plugin, broadcastChannel)
            _handler = broadcastChannel
            val broadcastLogChannel = BroadcastLogChannel(plugin, ProtocolProperties.PROJECT_LOG_CHANNEL)
            registerListener(plugin, broadcastLogChannel)
            _logHandler = broadcastLogChannel
        }
    }

    private fun registerCommand(plugin: Broadcast) {
        Logger.debug("registerCommand")
        proxy.pluginManager.apply {
            registerCommand(plugin, BroadcastCommand(plugin))
        }
    }
}