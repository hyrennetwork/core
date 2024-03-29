package net.hyren.core.shared.users.storage.dao

import net.hyren.core.shared.users.data.User
import net.hyren.core.shared.users.storage.table.UsersTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UserDAO(
    id: EntityID<UUID>,
) : UUIDEntity(id) {

    companion object : UUIDEntityClass<UserDAO>(UsersTable)

    var name by UsersTable.name
    var email by UsersTable.email
    var discordId by UsersTable.discordId
    var twoFactorAuthenticationEnabled by UsersTable.twoFactorAuthenticationEnabled
    var twoFactorAuthenticationCode by UsersTable.twoFactorAuthenticationCode
    var twitterAccessToken by UsersTable.twitterAccessToken
    var twitterTokenSecret by UsersTable.twitterTokenSecret
    var lastAddress by UsersTable.lastAddress
    var lastLobbyName by UsersTable.lastLobbyName
    var lastLoginAt by UsersTable.lastLoginAt
    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt

    fun toUser(): User = User(
        this.id,
        this.name,
        this.email,
        this.discordId,
        this.twoFactorAuthenticationEnabled,
        this.twoFactorAuthenticationCode,
        this.twitterAccessToken,
        this.twitterTokenSecret,
        this.lastAddress,
        this.lastLobbyName,
        this.lastLoginAt,
        this.createdAt,
        this.updatedAt
    )

}