package net.hyren.core.shared.servers.data

import net.hyren.core.shared.servers.ServerType
import org.apache.commons.lang3.StringUtils
import org.jetbrains.exposed.dao.id.EntityID

/**
 * @author SrGutyerrez
 **/
data class Server(
    val name: EntityID<String>,
    val displayName: String,
    val serverType: ServerType
) {

    fun getName() = this.name.value

    fun getFancyDisplayName() = StringUtils.replaceEach(
        this.displayName,
        arrayOf(
            "Rankup UP",
            "Factions"
        ),
        arrayOf(
            "R.",
            "F."
        )
    )

    override fun equals(other: Any?): Boolean {
        if (other === null) return false

        if (this === other) return true

        if (javaClass != other.javaClass) return false

        other as Server

        if (name != other.name) return false

        return true
    }

    override fun hashCode() = this.name.hashCode()

}