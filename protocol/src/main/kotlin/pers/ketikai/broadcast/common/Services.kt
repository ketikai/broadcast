package pers.ketikai.broadcast.common

import java.util.ServiceLoader

internal object Services {

    @JvmStatic
    fun <S: Any> singleton(serviceType: Class<S>, classLoader: ClassLoader? = null): S {
        val serviceLoader = ServiceLoader.load(serviceType, classLoader)
        val services = serviceLoader.toList()
        val serviceName = serviceType.simpleName
        services.isEmpty() && throw IllegalStateException("No $serviceName found!")
        if (services.size > 1) {
            val found = services.map {
                "$serviceName: ${it.javaClass.name} found!"
            }.toList().joinToString("\n")
            throw IllegalStateException("\n$found\nMultiple $serviceName found!")
        }
        return services.first()
    }
}