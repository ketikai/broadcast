package pers.ketikai.broadcast.protocol

class CommandOption(
    val ownership: Command,
    val name: String,
    val permissionNode: String = name,
    private val acceptor: CommandOptionAcceptor = CommandOptionAcceptor.DENY,
    private val convertor: CommandOptionConvertor = CommandOptionConvertor.DEFAULT,
    private val executor: CommandExecutor? = null
) {
//    val permission: String
//        get() {
//            val permission = ownership.permission
//            return if (permission.isEmpty()) permissionNode else "$permission.${permissionNode}"
//        }

    fun accept(context: Map<String, Any?>, sender: BroadcastSender, request: String): CommandAcceptResult {
        return acceptor.accept(context, sender, request).apply {
            executor = this@CommandOption.executor
        }
    }


    fun convert(context: Map<String, Any?>, sender: BroadcastSender, request: String): Map<String, Any?> {
        return convertor.convert(context, sender, request)
    }
}