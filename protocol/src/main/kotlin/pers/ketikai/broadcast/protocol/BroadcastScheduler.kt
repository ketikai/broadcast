package pers.ketikai.broadcast.protocol

interface BroadcastScheduler: BroadcastJobContainer {

    fun schedule(protocol: BroadcastProtocol): Pair<String, Boolean>

    fun unschedule(id: String): Boolean

    override fun remove(id: String): Boolean {
        return unschedule(id)
    }

    fun shutdown(waitForJobsToComplete: Boolean = false)
}