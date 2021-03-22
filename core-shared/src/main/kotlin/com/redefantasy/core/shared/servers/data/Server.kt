package com.redefantasy.core.shared.servers.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.redefantasy.core.shared.servers.ServerType
import org.apache.commons.lang3.StringUtils
import org.jetbrains.exposed.dao.id.EntityID

/**
 * @author SrGutyerrez
 **/
@JsonIgnoreProperties(value = ["name"])
data class Server(
    val name: EntityID<String>,
    val displayName: String,
    val serverType: ServerType
) {

    @JsonProperty(namespace = "name")
    fun getName() = this.name.value

    fun getFancyDisplayName() = StringUtils.replaceEach(
        this.displayName,
        arrayOf(
            "Rankup",
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