package com.redefantasy.core.spigot.misc.skin.command.subcommands

import com.redefantasy.core.shared.users.data.User
import com.redefantasy.core.spigot.command.CustomCommand
import com.redefantasy.core.spigot.misc.skin.command.SkinCommand
import com.redefantasy.core.spigot.misc.skin.services.SkinService
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender

/**
 * @author Gutyerrez
 */
class SkinRefreshCommand : CustomCommand("atualizar") {

	override fun getParent() = SkinCommand()

	override fun onCommand(
		commandSender: CommandSender,
		user: User?,
		args: Array<out String>
	): Boolean {
		commandSender.sendMessage(
			TextComponent(
				SkinService.refresh(user!!).message
			)
		)

		commandSender.sendMessage(
			TextComponent("§aSua pele foi alterada com sucesso, relogue para que ela atualize.")
		)
		return true
	}

}