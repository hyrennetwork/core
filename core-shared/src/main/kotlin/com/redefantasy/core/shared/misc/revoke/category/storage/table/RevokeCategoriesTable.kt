package com.redefantasy.core.shared.misc.revoke.category.storage.table

import com.redefantasy.core.shared.groups.Group
import com.redefantasy.core.shared.providers.databases.postgres.dao.StringTable

/**
 * @author SrGutyerrez
 **/
object RevokeCategoriesTable : StringTable("revoke_categories") {

    val displayName = varchar("display_name", 255)
    val description = varchar("description", 255)
    val group = enumerationByName("group_name", 255, Group::class)
    val enabled = bool("enabled")

}