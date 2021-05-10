package net.hyren.core.shared.misc.skin.controller

import com.fasterxml.jackson.databind.JsonNode
import com.sun.jdi.request.InvalidRequestStateException
import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.misc.skin.Skin
import okhttp3.Request

/**
 * @author Gutyerrez
 */
object SkinController {

	// Mojang
	private const val MOJANG_API_END_POINT = "https://api.mojang.com"
	private const val MOJANG_SESSION_END_POINT = "https://sessionserver.mojang.com"

	// Minetools
	private const val MINETOOLS_API_END_POINT = "https://api.minetools.eu"

	fun fetchSkinByName(name: String): Skin? {
		val request = Request.Builder()
			.url("$MOJANG_API_END_POINT/users/profiles/minecraft/$name")
			.header("Content-Type", "application/json")
			.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0")
			.get()
			.build()

		val response = CoreConstants.OK_HTTP.newCall(request).execute()

		lateinit var minecraftProfile: MinecraftProfile

		if (response.code != 200) {
			val request = Request.Builder()
				.url("$MINETOOLS_API_END_POINT/uuid/$name")
				.header("Content-Type", "application/json")
				.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0")
				.get()
				.build()

			val response = CoreConstants.OK_HTTP.newCall(request).execute()

			if (response.code != 200) throw InvalidRequestStateException() else {
				val bytes = response.body?.bytes()

				if (bytes?.isEmpty() == true) return null

				minecraftProfile = CoreConstants.JACKSON.readValue(
					bytes,
					MinecraftProfile::class.java
				)
			}
		} else {
			val bytes = response.body?.bytes()

			if (bytes?.isEmpty() == true) return null

			minecraftProfile = CoreConstants.JACKSON.readValue(
				bytes,
				MinecraftProfile::class.java
			)
		}

		val skin: () -> Skin? = invoker@{
			val request = Request.Builder()
				.url("$MOJANG_SESSION_END_POINT/session/minecraft/profile/${minecraftProfile.id}?unsigned=false")
				.header("Content-Type", "application/json")
				.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0")
				.get()
				.build()

			val response = CoreConstants.OK_HTTP.newCall(request).execute()

			lateinit var minecraftProfileData: MinecraftProfileData

			if (response.code != 200) {
				val request = Request.Builder()
					.url("$MINETOOLS_API_END_POINT/profile/${minecraftProfile.id}")
					.header("Content-Type", "application/json")
					.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0")
					.get()
					.build()

				val response = CoreConstants.OK_HTTP.newCall(request).execute()

				if (response.code != 200) throw InvalidRequestStateException() else {
					val bytes = response.body?.bytes()

					if (bytes?.isEmpty() == true) return@invoker null

					val jsonResponse = CoreConstants.JACKSON.readValue(
						bytes,
						JsonNode::class.java
					)

					minecraftProfileData = CoreConstants.JACKSON.readValue(
						jsonResponse.get("raw").asText(),
						MinecraftProfileData::class.java
					)
				}
			} else {
				val bytes = response.body?.bytes()

				if (bytes?.isEmpty() == true) return@invoker null

				minecraftProfileData = CoreConstants.JACKSON.readValue(
					bytes,
					MinecraftProfileData::class.java
				)
			}

			val properties = minecraftProfileData.properties[0]

			Skin(
				properties.value,
				properties.signature
			)
		}

		return skin.invoke()
	}

	internal open class MinecraftProfile(
		val id: String,
		val name: String
	)

	internal class MinecraftProfileData(
		id: String,
		name: String,
		val properties: Array<MinecraftProfileDataProperties>
	) : MinecraftProfile(
		id,
		name
	)

	internal data class MinecraftProfileDataProperties(
		val name: String,
		val value: String,
		val signature: String
	)

}

