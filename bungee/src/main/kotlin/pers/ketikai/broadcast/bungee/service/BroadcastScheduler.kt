package pers.ketikai.broadcast.bungee.service

import com.google.gson.Gson
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.JobKey
import org.quartz.JobListener
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.quartz.TriggerKey
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.GroupMatcher
import pers.ketikai.broadcast.bungee.Broadcast
import pers.ketikai.broadcast.bungee.crontab.BroadcastJob
import pers.ketikai.broadcast.protocol.BroadcastProtocol
import pers.ketikai.broadcast.protocol.BroadcastScheduler
import java.io.File
import java.security.MessageDigest
import java.util.Properties

class BroadcastScheduler(
    private val plugin: Broadcast
): BroadcastScheduler {
    private class BroadcastJobListener(private val plugin: Broadcast): JobListener {
        override fun getName(): String {
            return "BroadcastJobListener"
        }

        override fun jobToBeExecuted(context: JobExecutionContext) {
            context.jobDetail.jobDataMap["plugin"] = plugin
        }

        override fun jobExecutionVetoed(context: JobExecutionContext) {

        }

        override fun jobWasExecuted(context: JobExecutionContext, jobException: JobExecutionException?) {

        }
    }

    private val gson by lazy {
        Gson()
    }

    private val scheduler: Scheduler
    override val jobs: Map<String, String>
        get() {
            val details = scheduler.getJobKeys(GroupMatcher.anyJobGroup()).map { scheduler.getJobDetail(it) }
            return buildMap {
                for (detail in details) {
                    put(detail.key.name, detail.description)
                }
            }
        }

    init {
        val properties = Properties()
        val inputStream = this::class.java.getResourceAsStream("/quartz.properties")
        inputStream == null && throw IllegalStateException("Cannot load classpath:/quartz.properties.")
        inputStream.use(properties::load)
        val databaseFile = File(plugin.dataFolder, "h2")
        properties["org.quartz.dataSource.PUBLIC.URL"] = "jdbc:h2:${databaseFile.absolutePath}"
        val schedulerFactory = StdSchedulerFactory(properties)
        this.scheduler = schedulerFactory.scheduler.apply {
            listenerManager.addJobListener(BroadcastJobListener(plugin))
            start()
        }
    }

    private fun sha256(raw: String): String {
        return try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(raw.toByteArray())
            val digest = messageDigest.digest()
            digest.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            throw RuntimeException("Error hashing string", e)
        }
    }

    override fun schedule(protocol: BroadcastProtocol): Pair<String, Boolean> {
        val config = plugin.config
        val crontab = config.crontab
        val cronExpr = crontab[protocol.crontab]
            ?: throw IllegalArgumentException("Cannot find crontab expression for '$crontab'.")
        val json = gson.toJson(protocol)
        val id = sha256(json)
        val triggerKey = TriggerKey.triggerKey(id, null)
        if (scheduler.checkExists(triggerKey)) {
            return id to false
        }
        val trigger = TriggerBuilder.newTrigger().apply {
            withIdentity(triggerKey)
            withDescription(json)
            val cronSchedule = CronScheduleBuilder.cronSchedule(cronExpr)
            withSchedule(cronSchedule)
        }.build()
        val jobKey = JobKey(id, null)
        if (scheduler.checkExists(jobKey)) {
            return id to false
        }
        val jobDataMap = JobDataMap().apply {
            put("protocol", json)
        }
        val jobDetail = JobBuilder.newJob(BroadcastJob::class.java).apply {
            withIdentity(jobKey)
            withDescription(json)
            usingJobData(jobDataMap)
        }.build()
        scheduler.scheduleJob(jobDetail, trigger)
        return id to true
    }

    override fun unschedule(id: String): Boolean {
        val deleteJob = scheduler.deleteJob(JobKey(id, null))
        val unscheduleJob = scheduler.unscheduleJob(TriggerKey(id, null))
        return deleteJob || unscheduleJob
    }

    override fun clear() {
        scheduler.clear()
    }

    override fun shutdown(waitForJobsToComplete: Boolean) {
        scheduler.shutdown(waitForJobsToComplete)
    }
}