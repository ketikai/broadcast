package pers.ketikai.broadcast.bungee.service

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import pers.ketikai.broadcast.common.Logger

class Logger(private val sender: CommandSender = ProxyServer.getInstance().console):
    Logger {
    override fun println(vararg message: String) {
        message.map {
            TextComponent(it)
        }.forEach(sender::sendMessage)
    }
}