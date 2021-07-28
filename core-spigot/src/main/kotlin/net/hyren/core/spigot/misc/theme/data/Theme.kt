package net.hyren.core.spigot.misc.theme.data

import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.LocalSession
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.util.io.Closer
import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.applications.ApplicationType
import net.hyren.core.shared.applications.data.Application
import org.bukkit.Bukkit
import java.io.BufferedInputStream
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
        /*val chunk = Chunk(
            (Bukkit.getWorld(worldName) as CraftWorld).handle,
            x, z
        )

        FileInputStream(schematic).use {
            val nbtTagCompound = NBTCompressedStreamTools.a(it)

            val width = nbtTagCompound.getShort("Width")
            val height = nbtTagCompound.getShort("Height")
            val length = nbtTagCompound.getShort("Length")

            val blocks = nbtTagCompound.getByteArray("Blocks")
            val data = nbtTagCompound.getByteArray("Data")

            var addBlocks = byteArrayOf(0)

            if (nbtTagCompound.hasKey("AddBlocks")) {
                addBlocks = nbtTagCompound.getByteArray("AddBlocks")
            }

            val placeBlocks = ByteArray(blocks.size)

            blocks.forEachIndexed { index, byte ->
                if ((index shr 1) >= addBlocks.size) {
                    placeBlocks[index] = (byte and 0xFF.toByte())
                } else {
                    if ((index and 1) == 0) {
                        placeBlocks[index] = (((addBlocks[index shr 1] and 0x0F).toInt() shl 8) + (byte and 0xFF.toByte())).toByte()
                    } else {
                        placeBlocks[index] = (((addBlocks[index shr 1] and 0x0F).toInt() shl 4) + (byte and 0xFF.toByte())).toByte()
                    }
                }
            }

            var index = 0

            val world = Bukkit.getWorld("world")

            for (pasteX in 0..width) {
                for (pasteY in 0..height) {
                    for (pasteZ in 0..length) {
                        val block = world.getBlockAt(pasteX, 75 + pasteY, pasteZ)

                        block.typeId = placeBlocks[index].toInt()
                        block.data = data[index]

                        block.state.update()

                        index++
                    }
                }
            }
        }*/

        val _world = Bukkit.getWorld(worldName)
        val world = BukkitWorld(_world)

        val session = LocalSession()

        val closer = Closer.create()

        closer.use {
            val fileInputStream = closer.register(FileInputStream(schematic))
            val bufferedInputStream = closer.register(BufferedInputStream(
                fileInputStream
            ))

            val format = ClipboardFormat.SCHEMATIC

            val clipboardReader = format.getReader(bufferedInputStream)

            val worldData = world.worldData

            val clipboard = clipboardReader.read(worldData)

            session.clipboard = ClipboardHolder(
                clipboard, worldData
            )

            fileInputStream.close()
            bufferedInputStream.close()
        }

        val clipboardHolder = session.clipboard

        val clipboard = clipboardHolder.clipboard
        val region = clipboard.region

        val to = Vector(0, 75, 0)

        val editSession = EditSession(
            world,
            clipboard.region.area
        )

        val operation = clipboardHolder.createPaste(editSession, world.worldData).to(to).ignoreAirBlocks(true).build()

        Operations.complete(operation)
    }

    private fun Application.getThemesFolder(): String = when (applicationType) {
        ApplicationType.LOBBY -> "${CoreConstants.THEMES_FOLDER}/lobby"
        ApplicationType.SERVER_SPAWN, ApplicationType.SERVER_VIP -> "${CoreConstants.THEMES_FOLDER}/${name}"
        else -> throw RuntimeException("This application has not theme folder declared")
    }

}
