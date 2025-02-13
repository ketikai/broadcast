package pers.ketikai.broadcast.common

import java.io.File
import java.nio.file.Paths
import java.util.jar.JarFile
import kotlin.io.path.toPath

@Suppress("MemberVisibilityCanBePrivate")
object Bundled {
    private const val BUFFER_SIZE = 1024
    const val BUNDLED_DIR_PATH = "bundled"
    const val BUNDLED_DIR_PATH_LENGTH = BUNDLED_DIR_PATH.length

    @JvmStatic
    fun release(destDir: File, path: String = "/", overlay: Boolean = false) {
        Logger.debug("release bundled${if (overlay) " (overlay)" else ""}")
        destDir.exists() || destDir.mkdirs()
        val normalizedPath = Paths.get(BUNDLED_DIR_PATH, path).normalize().toString()
        val location = this::class.java.protectionDomain.codeSource.location
        val jarFile = location.toURI().toPath().toFile()
        val buffer = ByteArray(BUFFER_SIZE)
        val jar = JarFile(jarFile)
        jar.use {
            val entries = jar.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                var entryName = entry.name
                if (entryName.startsWith(normalizedPath)) {
                    entryName = entryName.substring(BUNDLED_DIR_PATH_LENGTH)
                    Logger.debug("bundled entry: $entryName")
                    val destFile = File(destDir, entryName)
                    if (entry.isDirectory) {
                        destFile.exists() || destFile.mkdirs()
                    } else {
                        destFile.parentFile.exists() || destFile.parentFile.mkdirs()
                        val exists = destFile.exists()
                        if (overlay || !exists) {
                            exists || destFile.createNewFile()
                            jar.getInputStream(entry).use { input ->
                                var read = input.read(buffer)
                                if (read != -1) {
                                    destFile.outputStream().use { output ->
                                        do {
                                            output.write(buffer, 0, read)
                                            output.flush()
                                            read = input.read(buffer)
                                        } while (read != -1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Logger.debug("release bundled done.")
    }
}