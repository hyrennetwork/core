package net.hyren.core.spigot.misc.theme.data

import kotlin.experimental.and
import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.applications.ApplicationType
import net.hyren.core.shared.applications.data.Application
import net.hyren.core.shared.misc.kotlin.sizedArray
import net.hyren.core.spigot.misc.asNMSWorld
import net.hyren.core.spigot.misc.theme.nbt.ByteArrayTag
import net.hyren.core.spigot.misc.theme.nbt.CompoundTag
import net.hyren.core.spigot.misc.theme.nbt.ShortTag
import net.hyren.core.spigot.misc.theme.nbt.stream.NBTInputStream
import net.minecraft.server.v1_8_R3.Block
import net.minecraft.server.v1_8_R3.BlockPosition
import net.minecraft.server.v1_8_R3.Material
import org.bukkit.Bukkit
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.Executors
import java.util.zip.DataFormatException
import java.util.zip.GZIPInputStream

/**
 * @author Gutyerrez
 */
data class Theme(
    val schematicName: String = "default.schematic"
) {

    private val EXECUTOR = Executors.newScheduledThreadPool(4)

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
            val nbtInputStream = NBTInputStream(
                GZIPInputStream(it)
            )

            val schematicTag = nbtInputStream.readTag() as CompoundTag

            nbtInputStream.close()

            if (schematicTag.name != "Schematic") {
                throw DataFormatException("Tag \"Schematic\" does not exists or is not first")
            }

            val schematic = schematicTag.value

            if (!schematic.containsKey("Blocks")) {
                throw DataFormatException("Schematic file is missing a \"Blocks\" tag")
            }

            val width = (schematic["Width"] as ShortTag).value
            val height = (schematic["Height"] as ShortTag).value
            val length = (schematic["Length"] as ShortTag).value

            val blocks = (schematic["Blocks"] as ByteArrayTag).value
            val data = (schematic["Data"] as ByteArrayTag).value

            var addId = ByteArray(0)

            val blocksIds = sizedArray<Short>(blocks.size)

            if (schematic.containsKey("AddBlocks")) {
                addId = (schematic["AddBlocks"] as ByteArrayTag).value
            }

            for (index in blocks.indices) {
                if ((index shr 1) >= addId.size) {
                    blocksIds[index] = (blocks[index] and 0xFF.toByte()).toShort()
                } else {
                    if ((index and 1) == 0) {
                        blocksIds[index] = (((addId[index shr 1].toInt() and 0x0F) shl 8) + (blocks[index].toInt() and 0xFF)).toShort()
                    } else {
                        blocksIds[index] = (((addId[index shr 1].toInt() and 0xF0) shl 4) + (blocks[index].toInt() and 0xFF)).toShort()
                    }
                }
            }

            val worldServer = Bukkit.getWorld(worldName).asNMSWorld()

//            EXECUTOR.execute {
                for (blockX in 0 until width) {
                    for (blockY in 0 until height) {
                        for (blockZ in 0 until length) {
                            val index = blockY * width * length + blockZ * width + blockX

                            if (!worldServer.chunkProviderServer.isChunkLoaded(blockX shr 4, blockZ shr 4)) {
                                worldServer.chunkProviderServer.loadChunk(blockX shr 4, blockZ shr 4)
                            }

                            val blockData = Block.getByCombinedId(blocksIds[index] + (data[index].toInt() shl 12))

                            if (blockData.block.material == Material.AIR) {
                                continue
                            }

                            if (!worldServer.chunkProviderServer.isChunkLoaded(blockX, blockZ)) {
                                worldServer.chunkProviderServer.loadChunk(blockX, blockZ)
                            }

                            worldServer.setTypeAndData(
                                BlockPosition(
                                    x + blockX - 157,
                                    y + blockY - 59,
                                    z + blockZ - 42,
                                ),
                                blockData,
                                2
                            )
                        }
                    }
                }
            }
//        }
    }

    private fun Application.getThemesFolder(): String = when (applicationType) {
        ApplicationType.LOBBY -> "${CoreConstants.THEMES_FOLDER}/lobby"
        ApplicationType.SERVER_SPAWN, ApplicationType.SERVER_VIP -> "${CoreConstants.THEMES_FOLDER}/${name}"
        else -> throw RuntimeException("This application has not theme folder declared")
    }

}
