package pers.ketikai.broadcast.protocol

import kotlin.reflect.KClass

interface CommandOptionConvertor {

    companion object {
        @JvmStatic
        val DEFAULT: CommandOptionConvertor = object: CommandOptionConvertor {
            override fun convert(context: Map<String, Any?>, sender: BroadcastSender, request: String): Map<String, Any?> {
                return emptyMap()
            }
        }

        @JvmStatic
        fun withRequest(key: String): CommandOptionConvertor {
            return object: CommandOptionConvertor {
                override fun convert(context: Map<String, Any?>, sender: BroadcastSender, request: String): Map<String, Any?> {
                    return mapOf(key to request)
                }
            }
        }

        @JvmStatic
        fun withRequest(key: String, enumType: KClass<out Enum<*>>): CommandOptionConvertor {
            return withRequest(key, enumType.java)
        }

        @JvmStatic
        fun withRequest(key: String, enumType: Class<out Enum<*>>): CommandOptionConvertor {
            return object: CommandOptionConvertor {
                override fun convert(context: Map<String, Any?>, sender: BroadcastSender, request: String): Map<String, Any?> {
                    return mapOf(key to java.lang.Enum.valueOf(enumType, request.uppercase()))
                }
            }
        }
    }

    fun convert(context: Map<String, Any?>, sender: BroadcastSender, request: String): Map<String, Any?>
}