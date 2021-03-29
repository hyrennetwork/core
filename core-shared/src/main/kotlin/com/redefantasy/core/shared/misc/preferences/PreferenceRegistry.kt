package com.redefantasy.core.shared.misc.preferences

import com.redefantasy.core.shared.misc.preferences.data.Preference

/**
 * @author Gutyerrez
 */
object PreferenceRegistry {

    private val PREFERENCES = mutableMapOf<String, Preference>()

    init {
        this.register(
            TELL_PREFERENCE,
            PLAYER_VISIBILITY
        )
    }

    fun register(vararg preferences: Preference) {
        preferences.forEach {
            this.PREFERENCES[it.name] = it
        }
    }

    fun fetchAll() = this.PREFERENCES.values.toTypedArray()

    fun fetchByName(name: String) = this.PREFERENCES[name]

}

val TELL_PREFERENCE = Preference("user-private-messages-preference")

val PLAYER_VISIBILITY = Preference("player-visibility-preference")

val FLY_IN_LOBBY = Preference("fly-in-lobby-preference")

val LOBBY_COMMAND_PROTECTION = Preference("lobby-command-protection-preference")
