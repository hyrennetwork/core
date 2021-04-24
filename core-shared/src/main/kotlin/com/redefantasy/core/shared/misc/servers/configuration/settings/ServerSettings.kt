package com.redefantasy.core.shared.misc.servers.configuration.settings

import com.fasterxml.jackson.annotation.JsonProperty
import com.redefantasy.core.shared.world.location.SerializedLocation

/**
 * @author Gutyerrez
 */
data class ServerSettings(
	@JsonProperty("max_players")
	val maxPlayers: Int,
	@JsonProperty("view_distance")
	val viewDistance: Int,
	@JsonProperty("spawn_location")
	val spawnLocation: SerializedLocation,
	@JsonProperty("npc_location")
	val npcLocation: SerializedLocation
)
