package net.hyren.core.shared.misc.skin.controller

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.misc.json.asString
import net.hyren.core.shared.misc.kotlin.sizedArray
import net.hyren.core.shared.misc.skin.Skin
import okhttp3.Request
import java.net.SocketTimeoutException

/**
 * @author Gutyerrez
 */
object SkinController {

	// Mojang
	private const val MOJANG_API_END_POINT = "https://api.mojang.com"
	private const val MOJANG_SESSION_END_POINT = "https://sessionserver.mojang.com"

	// Minetools
	private const val MINETOOLS_API_END_POINT = "https://api.minetools.eu"

	// Json
	private val json = Json { ignoreUnknownKeys = true }

	fun fetchSkinByName(name: String): Skin? {
		lateinit var minecraftProfile: MinecraftProfile

		try {
			val request = Request.Builder()
				.url("$MOJANG_API_END_POINT/users/profiles/minecraft/$name")
				.header("Content-Type", "application/json")
				.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0")
				.get()
				.build()

			val response = CoreConstants.OK_HTTP.newCall(request).execute()

			if (response.code != 200) {
				val request = Request.Builder().url("$MINETOOLS_API_END_POINT/uuid/$name").header("Content-Type", "application/json").header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0").get().build()
				val response = CoreConstants.OK_HTTP.newCall(request).execute()

				if (response.code != 200) {
					return null
				} else {
					val body = response.body?.string()

					if (body == null || body.isEmpty()) {
						return null
					}

					if (body.contains("null")) {
						return null
					}

					val jsonElement = json.decodeFromString<JsonElement>(body)

					if (jsonElement !is JsonObject || jsonElement is JsonNull) {
						return null
					}

					minecraftProfile = json.decodeFromJsonElement(jsonElement)
				}
			} else {
				val body = response.body?.string()

				if (body == null || body.isEmpty()) {
					return null
				}

				if (body.contains("null")) {
					return null
				}

				val jsonElement = json.decodeFromString<JsonElement>(body)

				if (jsonElement !is JsonObject || jsonElement is JsonNull) {
					return null
				}

				minecraftProfile = json.decodeFromJsonElement(jsonElement)
			}
		} catch (e: SocketTimeoutException) {
			return null
		}

		val skin: () -> Skin? = skin@ {
			lateinit var minecraftProfileData: MinecraftProfileData

			try {
				val request = Request.Builder()
					.url("$MOJANG_SESSION_END_POINT/session/minecraft/profile/${minecraftProfile.id}?unsigned=false")
					.header("Content-Type", "application/json")
					.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0")
					.get()
					.build()

				val response = CoreConstants.OK_HTTP.newCall(request).execute()

				if (response.code != 200) {
					val request = Request.Builder()
						.url("$MINETOOLS_API_END_POINT/profile/${minecraftProfile.id}")
						.header("Content-Type", "application/json")
						.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0")
						.get()
						.build()

					val response = CoreConstants.OK_HTTP.newCall(request).execute()

					if (response.code != 200) {
						return@skin null
					} else {
						val body = response.body?.string()

						if (body == null || body.isEmpty()) {
							return@skin null
						}

						if (body.contains("null")) {
							return@skin null
						}

						val jsonObject = json.decodeFromString<JsonObject>(body)

						if (!jsonObject.containsKey("raw") || jsonObject["raw"]?.jsonObject?.get("id") == null) {
							return@skin null
						}

						minecraftProfileData = json.decodeFromJsonElement(
							MinecraftProfileDataSerializer,
							jsonObject["raw"]!!
						)
					}
				} else {
					val body = response.body?.string()

					if (body == null || body.isEmpty()) {
						return@skin null
					}

					if (body.contains("null")) {
						return@skin null
					}

					val jsonObject = json.decodeFromString<JsonObject>(body)

					if (!jsonObject.containsKey("id") || jsonObject["id"] == null) {
						return@skin null
					}

					minecraftProfileData = json.decodeFromJsonElement(
						MinecraftProfileDataSerializer,
						jsonObject
					)
				}

				val properties = minecraftProfileData.properties[0]

				return@skin Skin(properties.value, properties.signature)
			} catch (e: SocketTimeoutException) {
				return@skin null
			}
		}

		return skin.invoke()
	}

	@Serializable
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

	@Serializable
	internal data class MinecraftProfileDataProperties(
		val name: String,
		val value: String,
		val signature: String
	)

	internal object MinecraftProfileDataSerializer : KSerializer<MinecraftProfileData> {
		override val descriptor: SerialDescriptor = ContextualSerializer(
			MinecraftProfileData::class,
			null,
			emptyArray()
		).descriptor

		override fun serialize(
			encoder: Encoder,
			value: MinecraftProfileData
		) = error("Unsupported")

		override fun deserialize(
			decoder: Decoder
		): MinecraftProfileData {
			val jsonDecoder = decoder as JsonDecoder

			val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

			val id = jsonObject["id"]!!.asString()
			val name = jsonObject["name"]!!.asString()
			val propertiesJsonArray = jsonObject["properties"]!!.jsonArray

			val properties = sizedArray<MinecraftProfileDataProperties>(
				propertiesJsonArray.size
			)

			propertiesJsonArray.forEachIndexed { index, it ->
				val jsonObject = it.jsonObject

				val name = jsonObject["name"]!!.asString()
				val signature = jsonObject["signature"]!!.asString()
				val value = jsonObject["value"]!!.asString()

				properties[index] = MinecraftProfileDataProperties(
					name,
					value,
					signature
				)
			}

			return MinecraftProfileData(
				id,
				name,
				properties
			)
		}

	}

}

