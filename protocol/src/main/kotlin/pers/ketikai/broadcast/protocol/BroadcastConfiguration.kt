package pers.ketikai.broadcast.protocol

interface BroadcastConfiguration {

    val servers: List<String>

    val crontab: Map<String, String>

    val target: Map<String, List<String>>

    fun reloadConfig()
}