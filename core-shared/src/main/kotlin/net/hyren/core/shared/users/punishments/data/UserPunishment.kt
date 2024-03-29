package net.hyren.core.shared.users.punishments.data

import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.groups.Group
import net.hyren.core.shared.misc.punish.PunishType
import net.hyren.core.shared.misc.punish.category.data.PunishCategory
import net.hyren.core.shared.misc.revoke.category.data.RevokeCategory
import net.hyren.core.shared.misc.utils.ChatColor
import net.hyren.core.shared.users.data.User
import org.jetbrains.exposed.dao.id.EntityID
import org.joda.time.DateTime
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author SrGutyerrez
 **/
data class UserPunishment(
    val id: EntityID<Int>,
    val userId: EntityID<UUID>,
    val stafferId: EntityID<UUID>,
    var startTime: DateTime? = null,
    val punishType: PunishType,
    val punishCategory: PunishCategory? = null,
    val duration: Long,
    val customReason: String? = null,
    val proof: String? = null,
    var revokeStafferId: EntityID<UUID>? = null,
    var revokeTime: DateTime? = null,
    var revokeCategory: RevokeCategory? = null,
    val hidden: Boolean = false,
    val perpetual: Boolean = false,
    val createdAt: DateTime = DateTime.now(),
    val updatedAt: DateTime? = null
) {

    fun getColor() = when {
        this.revokeTime !== null -> ChatColor.GRAY
        this.startTime === null -> ChatColor.YELLOW
        this.duration == -1L || this.startTime!! + this.duration > DateTime.now(
            CoreConstants.DATE_TIME_ZONE
        ) -> ChatColor.GREEN
        else -> ChatColor.RED
    }

    fun isBan(): Boolean {
        return this.punishType !== PunishType.MUTE
    }

    fun isActive(): Boolean {
        if (this.revokeTime !== null || this.startTime === null) return false

        if (this.punishType === PunishType.BAN) return true

        return if (this.duration == -1L) {
            true
        } else this.startTime!! + this.duration > DateTime.now(
            CoreConstants.DATE_TIME_ZONE
        )
    }

    fun isPending() = this.startTime === null

    fun isRevoked() = this.revokeTime !== null

    fun isFinalized() = this.startTime !== null && this.startTime!!.plus(this.duration) >= DateTime.now(
        CoreConstants.DATE_TIME_ZONE
    )

    fun isStrictActive() = this.startTime !== null && this.startTime!!.plus(this.duration) < DateTime.now(
            CoreConstants.DATE_TIME_ZONE
    )

    fun canBeRevokedFrom(revoker: User): Boolean {
        val currentDateTime = DateTime.now(
            CoreConstants.DATE_TIME_ZONE
        )

        if (revoker.hasGroup(Group.MASTER) || revoker.hasGroup(Group.MANAGER)) {
            return true
        } else if (revoker.hasGroup(Group.ADMINISTRATOR)) {
            return this.createdAt.plus(TimeUnit.DAYS.toMillis(3)) > currentDateTime
        } else if (revoker.hasGroup(Group.MODERATOR)) {
            return this.createdAt.plus(TimeUnit.HOURS.toMillis(12)) > currentDateTime
        } else if (revoker.hasGroup(Group.MANAGER)) {
            return this.createdAt.plus(TimeUnit.HOURS.toMillis(2)) > currentDateTime
        }

        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other === null) return false

        if (this === other) return true

        if (javaClass != other.javaClass) return false

        other as UserPunishment

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

}