package pers.ketikai.broadcast.bungee.channel

import com.google.gson.Gson
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import net.md_5.bungee.api.connection.Server
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import pers.ketikai.broadcast.bungee.Broadcast
import pers.ketikai.broadcast.bungee.service.BukkitBroadcastSender
import pers.ketikai.broadcast.common.LogLevel
import pers.ketikai.broadcast.common.Logger
import pers.ketikai.broadcast.generated.ProtocolProperties
import pers.ketikai.broadcast.protocol.BroadcastAction
import pers.ketikai.broadcast.protocol.BroadcastEvent
import pers.ketikai.broadcast.protocol.BroadcastProtocol
import pers.ketikai.broadcast.protocol.BroadcastProtocolHandler
import pers.ketikai.broadcast.protocol.BroadcastScheduler
import pers.ketikai.broadcast.protocol.BroadcastSender
import pers.ketikai.broadcast.protocol.BroadcastState
import java.io.ByteArrayInputStream
import java.io.Closeable
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BroadcastChannel(private val plugin: Broadcast, private val channel: String): Listener, BroadcastProtocolHandler, BroadcastScheduler, Closeable {

    private val gson by lazy {
        Gson()
    }

    private val scheduler: BroadcastScheduler = pers.ketikai.broadcast.bungee.service.BroadcastScheduler(plugin)

    init {
        plugin.proxy.registerChannel(channel)
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
        BroadcastEvent(protocol, BroadcastState.SEND).apply {
            fire() || return
            val logger = sender.logger
            val proxy = plugin.proxy
            val config = plugin.config
            proxy.servers.values.none {
                it.players.isNotEmpty()
            } && return
            if (!editor.crontab.equals("now", true)) {
                editor.build().apply {
                    try {
                        val (id, success) = schedule(this)
                        if (sender.isPlayer) {
                            val player = proxy.getPlayer(sender.name)
                            val component = if (success) {
                                TextComponent("${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.INFO.colorCode} Scheduled broadcast job: $id")
                            } else {
                                TextComponent("${ProtocolProperties.PROJECT_LANG_TITLE}${LogLevel.WARN.colorCode} Broadcast job '$id' already exists.")
                            }
                            component.clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id)
                            component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(TextComponent("Click to copy to clipboard.")))
                            player.sendMessage(component)
                        } else if (success) {
                            logger.info("Scheduled broadcast job: $id")
                        } else {
                            logger.error("Broadcast job '$id' already exists.")
                        }
                    } catch (e: IllegalArgumentException) {
                        logger.error(e)
                    }
                }
                return
            }
            val validServers = proxy.servers.keys
            val target = editor.target
            val validTarget: List<String> = if (target.equals("all", true)) {
                validServers.toList()
            } else {
                config.target[target]?.filter(validServers::contains)
                    ?: target.split(",").filter(validServers::contains)
            }
            if (validTarget.isEmpty()) {
                logger.error( "Target '$target' not found.")
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
//            editor.target(validTarget.joinToString(","))
            editor.build().apply {
                Logger.debug("Sending broadcast to servers: [${validTarget.joinToString(", ")}].")
                val finished = mutableListOf<String>()
                val data = gson.toJson(this).toByteArray()
                for (server in validTarget) {
                    val serverInfo = proxy.getServerInfo(server)
                    if (serverInfo.sendData(channel, data, false)) {
                        finished.add(server)
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
        BroadcastEvent(protocol, BroadcastState.RECEIVE).apply {
            fire() || return

            editor.build().apply {
                send(sender, this)
            }
        }
    }

    override val jobs: Map<String, String>
        get() = scheduler.jobs

    override fun schedule(protocol: BroadcastProtocol): Pair<String, Boolean> {
        return scheduler.schedule(protocol)
    }

    override fun unschedule(id: String): Boolean {
            return scheduler.unschedule(id)
    }

    override fun clear() {
          scheduler.clear()
    }

    override fun shutdown(waitForJobsToComplete: Boolean) {
        scheduler.shutdown(waitForJobsToComplete)
    }

    override fun close() {
        shutdown(true)
    }
}