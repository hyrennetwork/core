package net.hyren.core.spigot.misc.skin.inventory

import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.misc.utils.DateFormatter
import net.hyren.core.shared.users.data.User
import net.hyren.core.spigot.inventory.CustomInventory
import net.hyren.core.spigot.misc.player.openBook
import net.hyren.core.spigot.misc.player.openSignEditor
import net.hyren.core.spigot.misc.player.sendNonSuccessResponse
import net.hyren.core.spigot.misc.skin.services.SkinService
import net.hyren.core.spigot.misc.utils.BlockColor
import net.hyren.core.spigot.misc.utils.BookBuilder
import net.hyren.core.spigot.misc.utils.ItemBuilder
import net.hyren.core.spigot.sign.CustomSign
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author Gutyerrez
 */
class SkinsInventory(
	user: User
) : CustomInventory("Suas peles") {

	init {
		CoreProvider.Cache.Local.USERS_SKINS.provide().fetchByUserId(user.id)?.stream()
			?.sorted { o1, o2 ->
				o2.enabled.compareTo(o1.enabled) + o2.updatedAt.compareTo(o1.updatedAt)
			}?.forEach {
				this.addItem(
					ItemBuilder(Material.SKULL_ITEM)
						.durability(3)
						.skull(it.skin)
						.name(
							"§a${it.name}"
						).lore(
							arrayOf(
								"§fUsada pela última  vez em: §7${
									DateFormatter.formatToDefault(
										it.updatedAt,
										"às"
									)
								}",
								"",
								if (it.enabled) "§aSelecionada." else "§eClique para utilizar essa pele."
							)
						)
						.build()
				) { event ->
					val player = event.whoClicked as Player

					if (it.enabled) return@addItem

					player.closeInventory()

					val response = SkinService.changeSkin(
						user,
						it.name.value
					)

					if (response != SkinService.CommonResponse.CHANGING_SKIN_TO)
						return@addItem player.sendNonSuccessResponse(response)

					player.sendMessage(
						TextComponent(
							String.format(
								response.message,
								name
							)
						)
					)

					player.sendMessage(
						TextComponent("§aSua pele foi alterada com sucesso, relogue para que ela atualize.")
					)
				}
			}

		this.setItem(
			48,
			ItemBuilder(Material.BOOK_AND_QUILL)
				.name(
					"§eEscolher uma nova pele"
				).lore(
					arrayOf(
						"§7Você pode escolher uma nova pele",
						"§7para ser utilizada em sua conta.",
						"",
						"§fComando: §7/skin <nome>",
						"",
						"§aClique para escolher"
					)
				).build()
		) { event ->
			val player = event.whoClicked as Player

			player.closeInventory()

			val sign = CustomSign(player)
				.lines(
					TextComponent("§0Hey! Insira o"),
					TextComponent("§0nome da nova "),
					TextComponent("§0pele abaixo")
				).onUpdate { player, lines ->
					val skinName = lines[3].text

					if (skinName === null || skinName.isEmpty()) return@onUpdate

					val response = SkinService.changeSkin(
						user,
						skinName
					)

					if (response != SkinService.CommonResponse.CHANGING_SKIN_TO)
						return@onUpdate player.sendNonSuccessResponse(response)

					player.sendMessage(
						TextComponent(
							response.message.format(
								skinName
							)
						)
					)

					player.sendMessage(
						TextComponent("§aSua pele foi alterada com sucesso, relogue para que ela atualize.")
					)
				}

			player.openSignEditor(sign)
		}

		this.setItem(
			49,
			ItemBuilder(Material.BARRIER)
				.name(
					"§eAtualizar pele"
				).lore(
					arrayOf(
						"§7Isso irá restaurar a sua pele para a pele",
						"§7utilizada em sua conta do Minecraft. Caso",
						"§7você não possua uma conta, ficará a",
						"§7pele padrão do Minecraft.",
						"",
						"§fComando: §7/skin atualizar",
						"",
						"§aClique para atualizar."
					)
				).build()
		) { event ->
			val player = event.whoClicked as Player

			val book = BookBuilder()
				.title("Atualizar sua pele")
				.author("Hyren")
				.pages(
					arrayOf(
						ComponentBuilder()
							.append("Você está prestes a mudar sua pele para a original, tem certeza que deseja fazer isso?")
							.append("\n\n")
							.append("Caso sim, clique ")
							.append("§a§LAQUI")
							.event(
								ClickEvent(
									ClickEvent.Action.RUN_COMMAND,
									"/skin atualizar"
								)
							)
							.append("§r§0.")
							.append("\n")
							.append("Caso não, clique ")
							.append("§c§lAQUI")
							.event(
								ClickEvent(
									ClickEvent.Action.RUN_COMMAND,
									"/skin cancelar"
								)
							)
							.append("§r§0.")
							.append("\n\n")
							.append("Após a mudança só será possível mudar a sua pele novamente em ${SkinService.CHANGE_COOLDOWN} minutos.")
							.create()
					)
				).build()

			player.openBook(book)
		}

		this.setItem(
			50,
			ItemBuilder(Material.SKULL_ITEM)
				.durability(3)
				.skull(
					BlockColor.YELLOW
				).name(
					"§eAjuda"
				).lore(
					arrayOf(
						"§7As ações disponíveis neste menu também",
						"§7podem ser realizadas por comando.",
						"",
						"§fComando: §7/skin ajuda",
						"",
						"§aClique para listar os comandos."
					)
				).build()
		) { event ->
			val player = event.whoClicked as Player

			player.closeInventory()

			player.performCommand("skin ajuda")
		}
	}

}