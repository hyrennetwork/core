package com.redefantasy.core.bungee.misc.login.commands

import com.redefantasy.core.bungee.CoreBungeeConstants
import com.redefantasy.core.bungee.command.CustomCommand
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.commands.argument.Argument
import com.redefantasy.core.shared.commands.restriction.CommandRestriction
import com.redefantasy.core.shared.users.data.User
import com.redefantasy.core.shared.users.passwords.storage.dto.FetchUserPasswordByUserIdDTO
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * @author Gutyerrez
 */
class LoginCommand : CustomCommand("logar") {

    override fun getDescription() = "Autenticar sua conta."

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getArguments() = listOf(
        Argument("senha")
    )

    override fun onCommand(commandSender: CommandSender, user: User?, args: Array<out String>): Boolean {
        commandSender as ProxiedPlayer

        if (user === null) {
            commandSender.sendMessage(TextComponent("§cVocê não está registrado."))
            return false
        }

        val currentPassword = CoreProvider.Repositories.Postgres.USERS_PASSWORDS_REPOSITORY.provide().fetchByUserId(
            FetchUserPasswordByUserIdDTO(user.getUniqueId())
        ).stream()
            .filter { it.enabled }
            .findFirst()
            .orElse(null)

        if (currentPassword === null) {
            commandSender.sendMessage(TextComponent("§cVocê não está registrado."))
            return false
        }

        val successfully = user.attemptLogin(args[0])

        if (!successfully && user.loginAttempts.get() >= CoreBungeeConstants.MAX_LOGIN_ATTEMPTS) {
            commandSender.disconnect(
                TextComponent("§c§lREDE FANTASY"),
                TextComponent("\n"),
                TextComponent("§cVocê excedeu o número limite de ${CoreBungeeConstants.MAX_LOGIN_ATTEMPTS} tentativas de login, reconecte e tente novamente.")
            )

            user.loginAttempts.set(0)
            return false
        } else if (!successfully) {
            commandSender.sendMessage(TextComponent("§cSenha incorreta! Você tem mais ${(CoreBungeeConstants.MAX_LOGIN_ATTEMPTS % user.loginAttempts.get()) + 1} ${if ((CoreBungeeConstants.MAX_LOGIN_ATTEMPTS % user.loginAttempts.get()) + 1 > 1) "tentativas" else "tentativa"}."))
            return false
        }

        user.setLogged(successfully)

        // Send title to logged user
        println("${commandSender.name} logou")
        return true
    }

}