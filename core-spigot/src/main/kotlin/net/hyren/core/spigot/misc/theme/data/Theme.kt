package net.hyren.core.spigot.misc.theme.data

import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.applications.ApplicationType
import net.hyren.core.shared.applications.data.Application
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools
import java.io.File
import java.io.FileInputStream

/**
 * @author Gutyerrez
 */
data class Theme(
    val schematicName: String = "default.schematic"
) {

    private lateinit var schematic: File

    fun load() {
        val schematic = File(
            "${CoreProvider.application.getThemesFolder()}/$schematicName"
        )

        if (!schematic.exists()) {
            throw RuntimeException("Specified theme does not exists.")
        }

        this.schematic = schematic
    }

    fun paste(
        worldName: String = "world",
        x: Int,
        y: Int,
        z: Int
    ) {
        FileInputStream(schematic).use {
            val nbtTagCompound = NBTCompressedStreamTools::class.java.getMethod("a", FileInputStream::class.java).invoke(
                null, it
            )

            val getShort = nbtTagCompound::class.java.getMethod("getShort", String::class.java)
            val getByteArray = nbtTagCompound::class.java.getMethod("getByteArray", String::class.java)

            val width = getShort.invoke(nbtTagCompound, "Width") as Short
            val height = getShort.invoke(nbtTagCompound, "Height") as Short
            val length = getShort.invoke(nbtTagCompound, "Length") as Short

            val blocks = getByteArray.invoke(nbtTagCompound, "Blocks") as ByteArray
            val data = getByteArray.invoke(nbtTagCompound, "Data") as ByteArray

            val placeBlocks = ByteArray(blocks.size)

            println("Bora por")
        }
    }

    private fun Application.getThemesFolder(): String = when (applicationType) {
        ApplicationType.LOBBY -> "${CoreConstants.THEMES_FOLDER}/lobby"
        ApplicationType.SERVER_SPAWN, ApplicationType.SERVER_VIP -> "${CoreConstants.THEMES_FOLDER}/${name}"
        else -> throw RuntimeException("This application has not theme folder declared")
    }

}
