package com.redefantasy.core.shared.groups

import com.redefantasy.core.shared.misc.utils.ChatColor

/**
 * @author SrGutyerrez
 **/
enum class Group(
        var displayName: String? = null,
        var prefix: String? = null,
        var suffix: String? = null,
        var color: String? = null,
        var priority: Int? = null,
        var tabListOrder: Int? = null,
        var discordRoleId: Long? = null
) {

    GAME_MASTER,
    DIRECTOR,
    MANAGER,
    ADMINISTRATOR,
    MODERATOR,
    HELPER,
    BUILDER,
    YOUTUBE,
    ULTIMATE,
    PREMIUM,
    DEFAULT;

    fun getFancyDisplayName() = "${ChatColor.fromHEX(color ?: "")}$displayName"

}