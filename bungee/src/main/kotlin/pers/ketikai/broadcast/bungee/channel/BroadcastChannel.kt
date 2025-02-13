package pers.ketikai.broadcast.bungee.channel

import com.google.gson.Gson
import net.md_5.bungee.api.connection.Server
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import org.quartz.impl.StdSchedulerFactory
import pers.ketikai.broadcast.bungee.Broadcast
import pers.ketikai.broadcast.bungee.service.BukkitBroadcastSender
import pers.ketikai.broadcast.common.Logger
import pers.ketikai.broadcast.protocol.BroadcastAction
import pers.ketikai.broadcast.protocol.BroadcastEvent
import pers.ketikai.broadcast.protocol.BroadcastProtocol
import pers.ketikai.broadcast.protocol.BroadcastProtocolHandler
import pers.ketikai.broadcast.protocol.BroadcastSender
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

class BroadcastChannel(private val plugin: Broadcast, private val channel: String): Listener, BroadcastProtocolHandler {

    private val gson by lazy {
        Gson()
    }

    init {
        plugin.proxy.registerChannel(channel)
        val properties = Properties()
        val inputStream = this::class.java.getResourceAsStream("/quartz.properties")
        inputStream == null && throw IllegalStateException("Cannot load classpath:/quartz.properties.")
        inputStream.use(properties::load)
        val databaseFile = File(plugin.dataFolder, "h2.mv.db")
        properties["org.quartz.dataSource.PUBLIC.URL"] = "jdbc=h2=${databaseFile.absolutePath}"
        val schedulerFactory = StdSchedulerFactory(properties)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPluginMessageReceived(event: PluginMessageEvent) {
        event.tag != channel && return
        val sender = event.sender as? Server ?: return
        val data = event.data
        val broadcastProtocol = InputStreamReader(ByteArrayInputStream(data)).use {
            gson.fromJson(it, BroadcastProtocol::class.java)
        }

        val broadcastSender = BukkitBroadcastSender(sender, plugin)
        doReceive(broadcastSender, broadcastProtocol)
        event.isCancelled = true
    }

    override fun send(sender: BroadcastSender, protocol: BroadcastProtocol) {
        BroadcastEvent(protocol).apply {
            fire() || return
            if (!editor.crontab.equals("now", true)) {
                // TODO: crontab

                return
            }
            val logger = sender.logger
            val proxy = plugin.proxy
            val config = plugin.config
            val validServers = proxy.servers.keys
            val validTarget = config.target[editor.target]?.filter(validServers::contains)
                ?: editor.target.split(",").filter(validServers::contains)
            if (validTarget.isEmpty()) {
                logger.error( "Target '${editor.target}' not found.")
                return
            }
            var arguments = editor.arguments
            when (editor.action) {
                BroadcastAction.UNKNOWN -> {
                    logger.error( "Unknown broadcast action.")
                    return
                }
                BroadcastAction.NOTICE -> {
                    val notice = plugin.getNotice(arguments)
                    if (notice == null) {
                        logger.error( "Notice '$arguments' not found.")
                        return
                    }
                    arguments = notice.joinToString("\n")
                    if (arguments.isNotBlank()) {
                        arguments = setPlaceholders(sender, validTarget, arguments)
                    }
                    editor.arguments(arguments)
                }
                BroadcastAction.TIP -> {
                    editor.arguments(setPlaceholders(sender, validTarget, arguments))
                }
                BroadcastAction.SCRIPT -> {}
                else -> {
                    return
                }
            }
            editor.target(validTarget.joinToString(","))
            editor.build().apply {
                val mappings = LinkedHashSet(validTarget)
                val data = lazy {
                    gson.toJson(this).toByteArray()
                }
                val finished = mutableListOf<String>()
                if (mappings.contains("*")) {
                    for (server in proxy.servers.values) {
                        if (server.sendData(channel, data.value, false)) {
                            finished.add(server.name)
                        }
                    }
                } else {
                    for (it in mappings) {
                        val server = proxy.getServerInfo(it) ?: continue
                        if (server.sendData(channel, data.value, false)) {
                            finished.add(server.name)
                        }
                    }
                }
                Logger.info("Sent broadcast to servers: [${finished.joinToString(", ")}].")
            }
        }
    }

    private fun setPlaceholders(sender: BroadcastSender, target: List<String>, text: String): String {
        val senderIsAdmin = sender.isAdmin
        val senderIsPlayer = sender.isPlayer
        val placeholders = buildMap {
            val config = plugin.config.placeholder
            for (entry in config.custom.entries) {
                val placeholderValue = entry.value
                var value: String? = null
                if (senderIsPlayer) {
                    if (senderIsAdmin) {
                        value = placeholderValue.admin
                    }
                    if (value == null) {
                        value = placeholderValue.player
                    }
                } else {
                    value = placeholderValue.server
                }
                put(entry.key, value)
            }
            val timePattern = config.time
            val dateTimeFormatter = DateTimeFormatter.ofPattern(timePattern)
            val formatedTime = LocalDateTime.now().format(dateTimeFormatter)
            put("time", formatedTime)
            put("sender", sender.name)
            put("target", target.joinToString(","))
            put("target_first", target.first())
            put("target_last", target.last())
        }
        var ret = text
        for (entry in placeholders.entries) {
            ret = ret.replace("\${${entry.key}}", entry.value)
        }
        return ret
    }

    override fun receive(protocol: BroadcastProtocol) {
        val broadcastSender = pers.ketikai.broadcast.bungee.service.BroadcastSender()
        doReceive(broadcastSender, protocol)
    }

    private fun doReceive(sender: BroadcastSender, protocol: BroadcastProtocol) {
        BroadcastEvent(protocol).apply {
            fire() || return

            editor.build().apply {
                send(sender, this)
            }
        }
    }
}