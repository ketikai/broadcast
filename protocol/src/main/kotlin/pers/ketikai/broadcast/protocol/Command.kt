package pers.ketikai.broadcast.protocol

class Command(
    val name: String,
    aliases: Set<String> = emptySet(),
    val description: String = name,
    val permission: String = name
) {

    private val _aliases: Set<String> = LinkedHashSet(aliases.map(String::lowercase))

    val aliases: Set<String>
        get() = LinkedHashSet(_aliases)

    private val _optionGroups: LinkedHashSet<List<CommandOption>> = LinkedHashSet()
    private val optionGroups: List<List<CommandOption>>
        get() = _optionGroups.map { it.toList() }.toList()

    private var queue: MutableList<CommandOption>? = null

    fun option(
        name: String,
        permissionNode: String = name,
        acceptor: CommandOptionAcceptor = CommandOptionAcceptor.equalsIgnoreCase(name),
        convertor: CommandOptionConvertor = CommandOptionConvertor.DEFAULT,
        executor: CommandExecutor? = null
    ): Command {
        if (queue == null) {
            queue = mutableListOf()
        }
        queue?.add(CommandOption(this, name, permissionNode, acceptor, convertor, executor))
            ?: return option(name, permissionNode, acceptor, convertor, executor)
        return this
    }

    fun flush(): Command {
        val optionGroup = queue?.toList() ?: return this
        queue = null
        _optionGroups.add(optionGroup)
        return this
    }

    fun execute(context: Map<String, Any?>, sender: BroadcastSender, request: String, vararg arguments: String): Boolean {
        var permission: String
        if (!sender.isAdmin) {
            permission = this.permission
            sender.hasPermission(permission) || return CommandAcceptResult.permissionDenied(permission).execute(context, sender)
        }
        val lowercase = request.lowercase()
        if (lowercase == name || lowercase in _aliases) {
            return CommandAcceptResult.FAILURE.execute(context, sender)
        }

        var accept: Pair<Int, Pair<CommandAcceptResult, List<CommandOption>>>? = null
        for (optionGroup in optionGroups) {
            optionGroup.isEmpty() && continue
            val iterator = arguments.iterator()
            iterator.hasNext() || break
            var index = -1
            var lastResult: CommandAcceptResult? = null
            for (option in optionGroup) {
                val argument = iterator.next()
                if (!sender.isAdmin) {
                    permission = option.permission
                    if (!sender.hasPermission(permission)) {
                        lastResult = CommandAcceptResult.permissionDenied(permission)
                        break
                    }
                }
                lastResult = option.accept(context, sender, argument)
                lastResult.success || break
                index++
                iterator.hasNext() || break
            }
            index < 0 && continue
            lastResult as CommandAcceptResult
            if (accept == null) {
                accept = index to (lastResult to optionGroup.toList())
                continue
            }
            if (accept.second.first.success) {
                if (lastResult.success && accept.first < index) {
                    accept = index to (lastResult to optionGroup.toList())
                }
                continue
            }
            if (lastResult.success || accept.first < index) {
                accept = index to (lastResult to optionGroup.toList())
            }
        }
        if (accept == null) {
            return CommandAcceptResult.FAILURE.execute(context, sender)
        }
        val (index, pair) = accept
        val (result, group) = pair
        group.size <= index && throw IllegalStateException("Command option group size(${group.size}) must be greater than $index.")
        if (!result.success) {
            return result.execute(context, sender)
        }
        val local = LinkedHashMap(context)
        for (i in 0..index) {
            val option = group[i]
            val convert = option.convert(local, sender, arguments[i])
            local.putAll(convert)
        }
        val localArguments = arguments.copyOfRange(index + 1, arguments.size)
        return result.execute(local, sender, *localArguments)
    }
}