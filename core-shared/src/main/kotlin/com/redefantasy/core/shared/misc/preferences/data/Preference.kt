package com.redefantasy.core.shared.misc.preferences.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.redefantasy.core.shared.misc.preferences.PreferenceState
import java.io.Serializable

/**
 * @author SrGutyerrez
 **/
data class Preference(
    @JsonProperty
    val name: String,
    @param:JsonProperty("preference_state")
    @field:JsonProperty("preference_state")
    var preferenceState: PreferenceState = PreferenceState.ENABLED
) : Serializable {

    fun getStateColor() = preferenceState.getColor()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Preference) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return this.name.hashCode()
    }

}
