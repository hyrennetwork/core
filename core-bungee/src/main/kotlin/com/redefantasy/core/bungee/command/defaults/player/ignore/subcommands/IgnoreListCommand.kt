package com.redefantasy.core.bungee.command.defaults.player.ignore.subcommands

import com.redefantasy.core.bungee.command.CustomCommand
import com.redefantasy.core.bungee.command.defaults.player.ignore.IgnoreCommand
import com.redefantasy.core.shared.users.data.User
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ComponentBuilder

/**
 * @author Gutyerrez
 */
class IgnoreListCommand : CustomCommand("listar") {

    override fun getDescription() = "Mostra todos usuários já ignorados."

    override fun getParent() = IgnoreCommand()

    override fun onCommand(commandSender: CommandSender, user: User?, args: Array<out String>): Boolean? {
        val page = if (args.size == 1) args[0].toIntOrNull() ?: 0 else 0
        val message = ComponentBuilder()
            .append("\n")
            .append("§2Ignorados - Página 0/0")
            .append("\n\n")

        user!!.getIgnoredUsers()
            .subList(page, page * 10)
            .forEachIndexed { index, it ->
                message.append("§e - ${it.name}")

                if (index + 1 < user.getIgnoredUsers().size)
                    message.append("\n")
            }

        message.append("\n")

        commandSender.sendMessage(*message.create())
        return true
    }

}