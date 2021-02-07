package com.redefantasy.core.shared.misc.punish.category.storage.table

import com.redefantasy.core.shared.groups.Group
import com.redefantasy.core.shared.providers.databases.postgres.dao.StringTable
import com.redefantasy.core.shared.misc.exposed.array
import com.redefantasy.core.shared.misc.punish.durations.PunishDuration

/**
 * @author SrGutyerrez
 **/
object PunishCategoriesTable : StringTable("punish_categories") {

    val displayName = varchar("display_name", 255)
    val description = varchar("description", 255)
    val punishDurations = array<PunishDuration>("punish_durations", Array<PunishDuration>::class)
    val group = enumerationByName("group_name", 255, Group::class)
    val enabled = bool("enabled")

}