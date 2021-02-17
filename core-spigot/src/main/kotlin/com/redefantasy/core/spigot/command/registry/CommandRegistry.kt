package com.redefantasy.core.spigot.command.registry

import com.redefantasy.core.spigot.command.CustomCommand
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.craftbukkit.v1_8_R3.CraftServer

/**
 * @author Gutyerrez
 */
object CommandRegistry {

    fun registerCommand(customCommand: CustomCommand) {
        val craftServer = Bukkit.getServer() as CraftServer

        val simpleCommandMap = craftServer.commandMap

        val knowCommandsField = simpleCommandMap::class.java.getDeclaredField("knownCommands")

        knowCommandsField.isAccessible = true

        val commands = knowCommandsField.get(simpleCommandMap) as MutableMap<String, Command>

        commands.forEach { name, command ->
            if (name.contentEquals(customCommand.name)) {
                commands.remove(name)

                command.unregister(simpleCommandMap)
                command.aliases.forEach { commands.remove(it) }
            }
        }

        simpleCommandMap.register(customCommand.name, customCommand)
    }

}