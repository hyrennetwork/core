package com.redefantasy.core.bungee.command

import com.redefantasy.core.shared.commands.Commandable
import com.redefantasy.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.command.ConsoleCommandSender

/**
 * @author Gutyerrez
 */
abstract class CustomCommand(
    name: String
) : Command(name), Commandable<CommandSender> {

    override fun getSenderName(commandSender: CommandSender): String = commandSender.name

    override fun isPlayer(commandSender: CommandSender) = commandSender is ProxiedPlayer

    override fun isConsole(commandSender: CommandSender) = commandSender is ConsoleCommandSender

    abstract override fun onCommand(commandSender: CommandSender, user: User?, args: Array<out String>): Boolean?

    override fun execute(commandSender: CommandSender, args: Array<out String>) {
        this.executeRaw(
            commandSender,
            args
        )
    }

}