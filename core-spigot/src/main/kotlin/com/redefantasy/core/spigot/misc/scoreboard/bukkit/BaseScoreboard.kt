package com.redefantasy.core.spigot.misc.scoreboard.bukkit

import com.google.common.base.Splitter
import com.google.common.collect.Maps
import com.redefantasy.core.shared.misc.utils.ChatColor
import com.redefantasy.core.spigot.misc.scoreboard.Boardable
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.util.*

/**
 * @author Gutyerrez
 */
open class BaseScoreboard : Boardable {

    private val SCORE_BOARD_NAME = "main:${Bukkit.getPort()}"

    private val TEAMS = Maps.newTreeMap<Int, Team>()
    private val ENTRIES = Maps.newTreeMap<Int, String>()

    var scoreboard: Scoreboard
    var objective: Objective

    constructor() {
        this.scoreboard = Bukkit.getScoreboardManager().newScoreboard
        this.objective = this.scoreboard.registerNewObjective(
            this.SCORE_BOARD_NAME,
            "dummy"
        )

        this.objective.displaySlot = DisplaySlot.SIDEBAR
    }

    constructor(player: Player) {
        this.scoreboard = player.scoreboard
        this.objective = this.scoreboard.getObjective(this.SCORE_BOARD_NAME)
    }

    override fun set(
        score: Int,
        text: String
    ) {
        val text = ChatColor.translateAlternateColorCodes(
            '&',
            text
        )

        val iterator = Splitter.fixedLength(16).split(text).iterator()

        val prefixBuilder = StringBuilder()
        val suffixBuilder = StringBuilder()

        val index = prefixBuilder.length - 1

        if (prefixBuilder[index] == ChatColor.COLOR_CHAR) {
            prefixBuilder.deleteCharAt(index)

            suffixBuilder.insert(0, ChatColor.COLOR_CHAR)
        }

//        suffixBuilder.insert(0, ChatColor.)

        val prefix = prefixBuilder.toString()
        val suffix = suffixBuilder.toString()

        var team = this.TEAMS[score]
        val fresh = team === null

        val entry = if (fresh) {
            this.hash(score)
        } else ENTRIES[score]

        if (fresh) {
            team = this.scoreboard.registerNewTeam("$score-t")

            if (!team.hasEntry(entry)) team.addEntry(entry)

            this.TEAMS[score] = team
            this.ENTRIES[score] = entry
        }

        if (!Objects.equals(team?.prefix, prefix)) {
            team?.prefix = prefix
        }

        if (!Objects.equals(team?.suffix, suffix)) {
            team?.suffix = suffix
        }

        if (fresh) {
            this.objective.getScore(entry).score = score
        }
    }

    fun clear() {
        this.scoreboard.clearSlot(DisplaySlot.SIDEBAR)

        this.TEAMS.values.forEach { it.unregister() }

        this.TEAMS.clear()
        this.ENTRIES.clear()
    }

    fun add(text: String) {
        if (this.TEAMS.isEmpty()) {
            this.set(1, text)
        } else this.set(this.TEAMS.size + 1, text)
    }

    fun getEntry(score: Int): String? = this.ENTRIES[score]

    fun exists(score: Int) = this.ENTRIES.containsKey(score)

    fun send(players: Array<Player>) {
        players.forEach { it.scoreboard = this.scoreboard }
    }

    override fun setTitle(title: String) {
        this.objective.displayName = ChatColor.translateAlternateColorCodes(
            '&',
            title
        )
    }

    override fun reset(score: Int) {
        val entry = this.ENTRIES[score]

        if (entry === null) return

        if (this.TEAMS.containsKey(score)) {
            val team = this.TEAMS[score]

            if (team !== null) {
                this.scoreboard.resetScores(entry)

                this.ENTRIES.remove(score)

                team.unregister()
            }
        }
    }

    private fun hash(score: Int): String? {
        val builder = StringBuilder()

        for (character in Integer.toHexString(score).toCharArray()) {
            val color = ChatColor.getByChar(character)

            if (color != null) {
                builder.append(color.toString())
            }
        }

        return builder.toString()
    }

}