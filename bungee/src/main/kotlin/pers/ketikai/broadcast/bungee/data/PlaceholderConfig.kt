package pers.ketikai.broadcast.bungee.data

import net.md_5.bungee.config.Configuration

class PlaceholderConfig(
    private val configuration: Configuration
) {
    val time: String by lazy { configuration.getString("time", "yyyy-MM-dd HH:mm:ss") }

    val custom: Map<String, PlaceholderValue> by lazy {
        val section = configuration.getSection("custom")
        val keys = section.keys
        if (keys.isEmpty()) {
            return@lazy emptyMap()
        }
        return@lazy buildMap {
            for (key in keys) {
                val value = section.get(key) ?: continue
                when (value) {
                    is String -> put(key, PlaceholderValue(value, value, value))
                    is Configuration -> {
                        val server = value.getString("server")
                        val player = value.getString("player")
                        val admin = value.getString("admin")
                        if (server == null && player == null) continue
                        put(key, PlaceholderValue(server, player, admin))
                    }
                }
            }
        }
     }
}
