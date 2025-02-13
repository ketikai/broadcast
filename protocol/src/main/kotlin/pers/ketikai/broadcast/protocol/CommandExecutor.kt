package pers.ketikai.broadcast.protocol

interface CommandExecutor {

    companion object {
        @JvmStatic
        val SUCCESS: CommandExecutor = object: CommandExecutor {
            override fun execute(context: Map<String, Any?>, sender: BroadcastSender, vararg arguments: String): Boolean {
                return true
            }
        }

        @JvmStatic
        val FAILURE: CommandExecutor = object: CommandExecutor {
            override fun execute(context: Map<String, Any?>, sender: BroadcastSender, vararg arguments: String): Boolean {
                return false
            }
        }
    }

    fun execute(context: Map<String, Any?>, sender: BroadcastSender, vararg arguments: String): Boolean
}
