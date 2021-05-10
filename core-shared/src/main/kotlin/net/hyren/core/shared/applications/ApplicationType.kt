package net.hyren.core.shared.applications

import org.apache.commons.lang3.ArrayUtils

/**
 * @author SrGutyerrez
 **/
enum class ApplicationType {

    PROXY,

    LOGIN,

    LOBBY,
    
    PUNISHED_LOBBY,
    
    SERVER_SPAWN,
    SERVER_VIP,
    SERVER_WORLD,
    SERVER_WORLD_SPAWN,
    SERVER_WORLD_NORTH,
    SERVER_WORLD_EAST,
    SERVER_WORLD_WEST,
    SERVER_WORLD_SOUTH,
    SERVER_END,
    SERVER_MINE,
    SERVER_ARENA,
    SERVER_WAR,
    
    SERVER_TESTS,
    
    DUNGEON,
    BUILD,
    GENERIC;
    
    fun isTerrain() = ArrayUtils.contains(
            arrayOf(
                SERVER_WORLD,
                SERVER_WORLD_NORTH,
                SERVER_WORLD_EAST,
                SERVER_WORLD_WEST,
                SERVER_WORLD_SOUTH
            ),
            this
    )

}