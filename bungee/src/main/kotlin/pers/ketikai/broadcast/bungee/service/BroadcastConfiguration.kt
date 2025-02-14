package pers.ketikai.broadcast.bungee.service

import pers.ketikai.broadcast.bungee.Broadcast
import pers.ketikai.broadcast.protocol.BroadcastConfiguration

class BroadcastConfiguration(
    private val plugin: Broadcast
): BroadcastConfiguration {
//    override val servers: List<String>
//        get() = plugin.proxy.servers.keys.toList()
//    override val crontab: Map<String, String>
//        get() = plugin.config.crontab
//    override val target: Map<String, List<String>>
//        get() = plugin.config.target

    override fun reloadConfig() {
        plugin.reloadConfig()
    }
}