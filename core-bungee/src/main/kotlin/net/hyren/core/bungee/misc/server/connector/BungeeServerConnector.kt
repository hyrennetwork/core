package net.hyren.core.bungee.misc.server.connector

import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.applications.ApplicationType
import net.hyren.core.shared.users.data.User
import net.hyren.core.shared.users.storage.table.UsersTable
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.connection.ServerConnector
import org.jetbrains.exposed.dao.id.EntityID
import java.net.InetSocketAddress
import java.util.*

/**
 * @author Gutyerrez
 */
class BungeeServerConnector : ServerConnector {

	private val IGNORED_APPLICATIONS = arrayOf(
		ApplicationType.LOBBY,
		ApplicationType.PUNISHED_LOBBY
	)

	override fun fetchLobbyServer(userId: UUID?) = CoreConstants.fetchLobbyApplication()?.address

	override fun updateAndGetNext(
		proxiedPlayer: ProxiedPlayer,
		inetSocketAddress: InetSocketAddress
	): InetSocketAddress? {
		val user = CoreProvider.Cache.Local.USERS.provide().fetchById(proxiedPlayer.uniqueId)

		if (user != null && IGNORED_APPLICATIONS.contains(user.getConnectedBukkitApplication()?.applicationType)) return null

		val application = CoreProvider.Cache.Local.APPLICATIONS.provide().fetchByAddress(
			inetSocketAddress
		) ?: return null

		if (IGNORED_APPLICATIONS.contains(application.applicationType)) {
			return null
		}

		val targetApplication = CoreConstants.fetchLobbyApplication()

		if (user != null && user.getConnectedBukkitApplication() == targetApplication) {
			return null
		}

		return targetApplication?.address
	}

	override fun changedUserApplication(
		proxiedPlayer: ProxiedPlayer,
		bukkitApplicationAddress: InetSocketAddress
	) {
		val bukkitApplication = CoreProvider.Cache.Local.APPLICATIONS.provide().fetchByAddress(
			bukkitApplicationAddress
		)

		if (bukkitApplication === null) {
			val disconnectMessage = ComponentBuilder()
				.append(CoreConstants.Info.ERROR_SERVER_NAME)
				.append("\n\n")
				.append("§cNão foi possível localizar a aplicação.")
				.create()

			proxiedPlayer.disconnect(*disconnectMessage)
			return
		}

		var user = CoreProvider.Cache.Local.USERS.provide().fetchById(proxiedPlayer.uniqueId)

		if (user === null) user = User(
			EntityID(proxiedPlayer.uniqueId, UsersTable),
			proxiedPlayer.name
		)

		CoreProvider.Cache.Redis.USERS_STATUS.provide().create(
			user,
			bukkitApplication,
			proxiedPlayer.pendingConnection.version
		)
	}

}