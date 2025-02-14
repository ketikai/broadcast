package pers.ketikai.broadcast.protocol

@FunctionalInterface
interface CommandOptionAcceptor {

    companion object {

        @JvmStatic
        val DENY: CommandOptionAcceptor = object: CommandOptionAcceptor {
            override fun accept(context: Map<String, Any?>, sender: BroadcastSender, request: String): CommandAcceptResult {
                return CommandAcceptResult.FAILURE
            }
        }

        @JvmStatic
        val ACCEPT: CommandOptionAcceptor = object: CommandOptionAcceptor {
            override fun accept(context: Map<String, Any?>, sender: BroadcastSender, request: String): CommandAcceptResult {
                return CommandAcceptResult.SUCCESS
            }
        }

        @JvmStatic
        fun equals(string: String, not: Boolean = false): CommandOptionAcceptor {
            return object: CommandOptionAcceptor {
                override fun accept(
                    context: Map<String, Any?>,
                    sender: BroadcastSender,
                    request: String
                ): CommandAcceptResult {
                    var success = string == request
                    success = if (not) !success else success
                    return CommandAcceptResult.auto(success)
                }
            }
        }

        @JvmStatic
        fun equalsIgnoreCase(string: String, not: Boolean = false): CommandOptionAcceptor {
            return object: CommandOptionAcceptor {
                override fun accept(
                    context: Map<String, Any?>,
                    sender: BroadcastSender,
                    request: String
                ): CommandAcceptResult {
                    var success = string.equals(request, true)
                    success = if (not) !success else success
                    return CommandAcceptResult.auto(success)
                }
            }
        }

        @JvmStatic
        fun contains(collection: Collection<String>, not: Boolean = false): CommandOptionAcceptor {
            return object: CommandOptionAcceptor {
                override fun accept(
                    context: Map<String, Any?>,
                    sender: BroadcastSender,
                    request: String
                ): CommandAcceptResult {
                    var success = collection.contains(request)
                    success = if (not) !success else success
                    return CommandAcceptResult.auto(success)
                }
            }
        }

        @JvmStatic
        fun containsIgnoreCase(collection: Collection<String>, not: Boolean = false): CommandOptionAcceptor {
            return object: CommandOptionAcceptor {
                override fun accept(
                    context: Map<String, Any?>,
                    sender: BroadcastSender,
                    request: String
                ): CommandAcceptResult {
                    var success = false
                    for (s in collection) {
                        if (s.equals(request, true)) {
                            success = true
                            break
                        }
                    }
                    success = if (not) !success else success
                    return CommandAcceptResult.auto(success)
                }
            }
        }
    }

    fun accept(context: Map<String, Any?>, sender: BroadcastSender, request: String): CommandAcceptResult
}