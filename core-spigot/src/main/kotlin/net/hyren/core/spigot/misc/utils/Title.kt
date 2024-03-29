package net.hyren.core.spigot.misc.utils

import net.hyren.core.shared.misc.utils.ChatColor
import net.hyren.core.spigot.misc.player.sendPacket
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_8_R3.Packet
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle
import org.bukkit.entity.Player

/**
 * @author Gutyerrez
 */
class Title(
    var title: String?,
    var subTitle: String?,
    val fadeIn: Int,
    val fadeOut: Int,
    val stay: Int
) {

    companion object {

        fun clear(player: Player) {
            val packet = PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.CLEAR,
                arrayOf(),
                0,
                0,
                0
            )

            player.sendPacket(packet)
        }

    }

    fun sendToPlayer(player: Player) {
        var packet: Packet<*>

        if (title !== null) {
            packet = PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TIMES,
                arrayOf(
                    TextComponent(ChatColor.translateAlternateColorCodes('&', title!!))
                ),
                fadeIn,
                stay,
                fadeOut
            )

            player.sendPacket(packet)

            packet = PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TITLE,
                arrayOf(
                    TextComponent(ChatColor.translateAlternateColorCodes('&', title!!))
                ),
                fadeIn,
                stay,
                fadeOut
            )

            player.sendPacket(packet)
        }

        if (subTitle !== null) {
            packet = PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TIMES,
                arrayOf(
                    TextComponent(ChatColor.translateAlternateColorCodes('&', subTitle!!))
                ),
                fadeIn,
                stay,
                fadeOut
            )

            player.sendPacket(packet)

            packet = PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                arrayOf(
                    TextComponent(ChatColor.translateAlternateColorCodes('&', subTitle!!))
                ),
                fadeIn,
                stay,
                fadeOut
            )

            player.sendPacket(packet)
        }
    }

}