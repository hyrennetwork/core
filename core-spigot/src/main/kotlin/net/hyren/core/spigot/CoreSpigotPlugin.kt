package net.hyren.core.spigot

import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.applications.ApplicationType
import net.hyren.core.shared.applications.status.ApplicationStatus
import net.hyren.core.shared.applications.status.task.ApplicationStatusTask
import net.hyren.core.shared.scheduler.AsyncScheduler
import net.hyren.core.shared.wrapper.CoreWrapper
import net.hyren.core.spigot.echo.packets.listener.SoundEchoPacketListener
import net.hyren.core.spigot.echo.packets.listener.TitleEchoPacketListener
import net.hyren.core.spigot.listeners.GenericListener
import net.hyren.core.spigot.misc.plugin.CustomPlugin
import net.hyren.core.spigot.misc.utils.PacketEvent
import net.hyren.core.spigot.misc.utils.PacketListener
import net.hyren.core.spigot.sign.CustomSign
import net.hyren.core.spigot.wrapper.SpigotWrapper
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.TimeUnit

/**
 * @author Gutyerrez
 */
class CoreSpigotPlugin : CustomPlugin(true) {

    companion object {

        lateinit var instance: CustomPlugin

    }

    init {
        instance = this
    }

    private var onlineSince = 0L

    override fun onEnable() {
        super.onEnable()

        CoreSpigotProvider.prepare()

        this.onlineSince = System.currentTimeMillis()

        CoreWrapper.WRAPPER = SpigotWrapper()

        CoreProvider.Databases.Redis.ECHO.provide().registerListener(TitleEchoPacketListener())
        CoreProvider.Databases.Redis.ECHO.provide().registerListener(SoundEchoPacketListener())

        val pluginManager = Bukkit.getServer().pluginManager

        /**
         * Spigot listeners
         */

        pluginManager.registerEvents(GenericListener(), this)
        pluginManager.registerEvents(object : Listener {
            @EventHandler
            fun on(
                event: PlayerJoinEvent
            ) { event.joinMessage = null }

            @EventHandler
            fun on(
                event: PlayerQuitEvent
            ) { event.quitMessage = null }
        }, this)

        /**
         * Scuti Block Exploits
         */

        // later

        /**
         * Protocol
         */

        CoreSpigotConstants.PROTOCOL_HANDLER.registerListener(
            object : PacketListener() {

                override fun onReceive(
                    event: PacketEvent
                ) {
                    val player = event.player
                    val packet = event.packet

                    if (packet is PacketPlayInUpdateSign) {
                        if (player.hasMetadata("custom-sign")) {
                            val customSign = player.getMetadata("custom-sign")[0].value() as CustomSign
                            val texts = packet.b()

                            customSign.UPDATED_LISTENER?.accept(
                                player,
                                texts
                            )

                            player.removeMetadata("custom-sign", this@CoreSpigotPlugin)
                        }
                    }
                }

            }
        )

        if (CoreProvider.application.applicationType == ApplicationType.LOBBY) {

            /**
             * World settings
             */
            Bukkit.getServer().worlds.forEach {
                it.isAutoSave = true

                it.isThundering = false
                it.weatherDuration = 0

                it.ambientSpawnLimit = 0
                it.animalSpawnLimit = 0
                it.monsterSpawnLimit = 0

                it.setTicksPerAnimalSpawns(99999)
                it.setTicksPerMonsterSpawns(99999)

                it.setStorm(false)

                it.setGameRuleValue("randomTickSpeed", "-1")
                it.setGameRuleValue("mobGriefing", "false")
                it.setGameRuleValue("doMobSpawning", "false")
                it.setGameRuleValue("doMobLoot", "false")
                it.setGameRuleValue("doFireTick", "false")
                it.setGameRuleValue("doDaylightCycle", "false")

                it.time = 1200
            }

            AsyncScheduler.scheduleAsyncRepeatingTask(
                object : ApplicationStatusTask(
                    ApplicationStatus(
                        CoreProvider.application.name,
                        CoreProvider.application.applicationType,
                        CoreProvider.application.server,
                        CoreProvider.application.address,
                        this.onlineSince
                    )
                ) {
                    override fun buildApplicationStatus(
                        applicationStatus: ApplicationStatus
                    ) {
                        val runtime = Runtime.getRuntime()

                        applicationStatus.heapSize = runtime.totalMemory()
                        applicationStatus.heapMaxSize = runtime.maxMemory()
                        applicationStatus.heapFreeSize = runtime.freeMemory()

                        applicationStatus.onlinePlayers = Bukkit.getOnlinePlayers().size
                    }
                },
                0,
                1,
                TimeUnit.SECONDS
            )
        }
    }

}