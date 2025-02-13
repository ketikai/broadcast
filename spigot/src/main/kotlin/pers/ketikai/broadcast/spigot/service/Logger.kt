package pers.ketikai.broadcast.spigot.service

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import pers.ketikai.broadcast.common.Logger

class Logger(private val sender: CommandSender = Bukkit.getConsoleSender()):
    Logger {

    override fun println(vararg message: String) {
        sender.sendMessage(message)
    }
}