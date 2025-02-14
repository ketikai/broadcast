package pers.ketikai.broadcast.bungee.crontab

import com.google.gson.Gson
import org.quartz.Job
import org.quartz.JobExecutionContext
import pers.ketikai.broadcast.bungee.Broadcast
import pers.ketikai.broadcast.protocol.BroadcastProtocol

open class BroadcastJob: Job {

    private val gson by lazy {
        Gson()
    }

    override fun execute(context: JobExecutionContext) {
        val jobDataMap = context.jobDetail.jobDataMap
        val json = jobDataMap["protocol"] ?: throw IllegalArgumentException("protocol is null")
        json as String
        val protocol = gson.fromJson(json, BroadcastProtocol::class.java).run {
            this == null && throw IllegalArgumentException("protocol is null")
            return@run BroadcastProtocol.builder().by(this).crontab("now").build()
        }
        val plugin = jobDataMap["plugin"] ?: throw IllegalArgumentException("plugin is null")
        plugin as Broadcast
        plugin.handler.receive(protocol)
    }
}