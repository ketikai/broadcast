package pers.ketikai.broadcast.protocol

@Suppress("MemberVisibilityCanBePrivate")
class BroadcastCommand(
    val name: String,
    val aliases: Set<String>,
    private val broadcastConfiguration: BroadcastConfiguration,
    private val broadcastProtocolHandler: BroadcastProtocolHandler,
    private val broadcastJobContainer: BroadcastJobContainer
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

        option("job")
        option("list", executor = object : CommandExecutor {
            override fun execute(
                context: Map<String, Any?>,
                sender: BroadcastSender,
                vararg arguments: String
            ): Boolean {
                val logger = sender.logger
                val jobs = broadcastJobContainer.jobs
                if (jobs.isEmpty()) {
                    logger.info("No jobs.")
                    return true
                }
                val message = jobs.map { (id, desc) -> "  id: $id\n  description: $desc" }.joinToString("\n------\n")
                logger.info("\n$message")
                return true
            }
        })
        flush()

        option("job")
        option("clear", executor = object : CommandExecutor {
            override fun execute(
                context: Map<String, Any?>,
                sender: BroadcastSender,
                vararg arguments: String
            ): Boolean {
                val logger = sender.logger
                broadcastJobContainer.clear()
                logger.info("Cleared jobs.")
                return true
            }
        })
        flush()

        option("job")
        option("remove", executor = object : CommandExecutor {
            override fun execute(
                context: Map<String, Any?>,
                sender: BroadcastSender,
                vararg arguments: String
            ): Boolean {
                val logger = sender.logger
                if (arguments.isEmpty()) {
                    logger.error("Specify job id.")
                    return false
                }
                val id = arguments[0]
                if (broadcastJobContainer.remove(id)) {
                    logger.info("Removed job $id.")
                } else {
                    logger.info("Job $id not found.")
                }
                return true
            }
        })
        flush()

        option("commit")
        option("crontab", acceptor = CommandOptionAcceptor.ACCEPT, convertor = CommandOptionConvertor.withRequest("crontab"))
        option("target", acceptor = CommandOptionAcceptor.ACCEPT, convertor = CommandOptionConvertor.withRequest("target"))
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
                    arguments(arguments.joinToString(" "))
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