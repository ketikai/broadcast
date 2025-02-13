package pers.ketikai.broadcast.protocol

@Suppress("MemberVisibilityCanBePrivate")
class BroadcastCommand(
    val name: String,
    val aliases: Set<String>,
    private val broadcastConfiguration: BroadcastConfiguration,
    private val broadcastProtocolHandler: BroadcastProtocolHandler
) {

    private val delegate: Command = Command(name, aliases = aliases).apply {
        option("reload", executor = object: CommandExecutor {
            override fun execute(
                context: Map<String, Any?>,
                sender: BroadcastSender,
                vararg arguments: String
            ): Boolean {
                broadcastConfiguration.reloadConfig()
                return true
            }
        })
        flush()

        option("crontab", acceptor = CommandOptionAcceptor.ACCPET, convertor = CommandOptionConvertor.withRequest("crontab"))
        option("target", acceptor = CommandOptionAcceptor.ACCPET, convertor = CommandOptionConvertor.withRequest("target"))
        option("action", acceptor = CommandOptionAcceptor.containsIgnoreCase(
            BroadcastAction.entries.map { it.name }
        ), convertor = CommandOptionConvertor.withRequest("action", BroadcastAction::class.java), executor = object : CommandExecutor {
            override fun execute(
                context: Map<String, Any?>,
                sender: BroadcastSender,
                vararg arguments: String
            ): Boolean {
                val crontab = context["crontab"] as String
                val target = context["target"] as String
                val action = context["action"] as BroadcastAction
                val protocol = BroadcastProtocol.builder().apply {
                    crontab(crontab)
                    target(target)
                    action(action)
                }.build()
                broadcastProtocolHandler.send(sender,  protocol)
                return true
            }
        })
        flush()
    }

    fun execute(context: Map<String, Any?>, sender: BroadcastSender, request: String, vararg arguments: String): Boolean {
        return delegate.execute(context, sender, request, *arguments)
    }
}