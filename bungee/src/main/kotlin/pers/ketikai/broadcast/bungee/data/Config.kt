package pers.ketikai.broadcast.bungee.data

import net.md_5.bungee.config.Configuration

class Config(private val configuration: Configuration) {

    val debug: Boolean by lazy { configuration.getBoolean("debug", true) }

     val crontab: Map<String, String> by lazy {
        val section = configuration.getSection("crontab")
        val keys = section.keys
        if (keys.isEmpty()) {
            return@lazy emptyMap()
        }
        return@lazy buildMap {
            for (key in keys) {
                val value = section.getString(key)
                if (value != null) put(key, value)
            }
        }
    }

     val target: Map<String, List<String>> by lazy {
        val section = configuration.getSection("target")
        val keys = section.keys
        if (keys.isEmpty()) {
            return@lazy emptyMap()
        }
        return@lazy buildMap {
            for (key in keys) {
                val value = section.getStringList(key)
                if (value != null && value.isNotEmpty()) put(key, value)
            }
        }
    }

    val placeholder: PlaceholderConfig by lazy {
        val section = configuration.getSection("placeholder")
        return@lazy PlaceholderConfig(section)
     }
}