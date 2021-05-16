package net.hyren.core.spigot

import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.hyren.core.shared.misc.json.*
import net.hyren.core.shared.providers.IProvider
import net.hyren.core.shared.providers.cache.local.LocalCacheProvider
import net.hyren.core.shared.providers.databases.mariadb.providers.MariaDBRepositoryProvider
import net.hyren.core.shared.world.location.SerializedLocation
import net.hyren.core.spigot.misc.server.configuration.cache.local.ServersConfigurationsLocalCache
import net.hyren.core.spigot.misc.server.configuration.data.ServerConfiguration
import net.hyren.core.spigot.misc.server.configuration.settings.ServerSettings
import net.hyren.core.spigot.misc.server.configuration.storage.repositories.IServersConfigurationRepository
import net.hyren.core.spigot.misc.server.configuration.storage.repositories.implementations.MariaDBServersConfigurationRepository
import org.bukkit.inventory.ItemStack

/**
 * @author Gutyerrez
 */
object CoreSpigotProvider {

    fun prepare() {
        Serializers.CUSTOM_SERIALIZERS.prepare()

        Repositories.MariaDB.SERVERS_CONFIGURATION_REPOSITORY.prepare()

        Cache.Local.SERVER_CONFIGURATION.prepare()
    }

    object Serializers {

        val CUSTOM_SERIALIZERS = object : IProvider<Any> {

            override fun prepare() {
                KJson.registerSerializer {
                    // ItemStack serializer
                    contextual(
                        ItemStack::class,
                        object : KSerializer<ItemStack>() {
                            override fun serialize(
                                jsonEncoder: JsonEncoder,
                                value: ItemStack
                            ) {
                                println("Serializar")
                            }

                            override fun deserialize(
                                jsonDecoder: JsonDecoder
                            ): ItemStack = error("Não implementado ainda")
                        }
                    )

                    // SerializedLocation serializer
                    contextual(
                        SerializedLocation::class,
                        object : KSerializer<SerializedLocation>() {
                            override fun serialize(
                                jsonEncoder: JsonEncoder,
                                value: SerializedLocation
                            ) {
                                jsonEncoder.encodeJsonElement(buildJsonObject {
                                    put("application_name", value.applicationName)
                                    put("world_name", value.worldName)
                                    put("x", value.x)
                                    put("y", value.y)
                                    put("z", value.z)
                                    put("yaw", value.yaw)
                                    put("pitch", value.pitch)
                                })
                            }

                            override fun deserialize(
                                jsonDecoder: JsonDecoder
                            ): SerializedLocation {
                                val jsonObject = jsonDecoder.decodeJsonElement().asJsonObject()

                                return SerializedLocation(
                                    jsonObject.get("application_name")?.asString(),
                                    jsonObject.getValue("world_name").asString(),
                                    jsonObject.getValue("x").asDouble(),
                                    jsonObject.getValue("y").asDouble(),
                                    jsonObject.getValue("z").asDouble(),
                                    jsonObject.getValue("yaw").asFloat(),
                                    jsonObject.getValue("pitch").asFloat()
                                )
                            }
                        }
                    )

                    // ServerConfiguration serializer
                    contextual(
                        ServerConfiguration::class,
                        object : KSerializer<ServerConfiguration>() {
                            override fun serialize(
                                jsonEncoder: JsonEncoder,
                                value: ServerConfiguration
                            ) = error("Unsupported")

                            override fun deserialize(
                                jsonDecoder: JsonDecoder
                            ): ServerConfiguration {
                                val jsonObject = jsonDecoder.decodeJsonElement().asJsonObject()

                                return ServerConfiguration(
                                    ServerSettings(
                                        jsonObject.getValue("server_settings").asJsonObject().getValue("max_players").asInt(),
                                        jsonObject.getValue("server_settings").asJsonObject().getValue("view_distance").asInt(),
                                        SerializedLocation(
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("spawn_location").asJsonObject().getValue("world_name").asString(),
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("spawn_location").asJsonObject().getValue("x").asDouble(),
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("spawn_location").asJsonObject().getValue("y").asDouble(),
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("spawn_location").asJsonObject().getValue("z").asDouble(),
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("spawn_location").asJsonObject().getValue("yaw").asFloat(),
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("spawn_location").asJsonObject().getValue("pitch").asFloat(),
                                        ),
                                        SerializedLocation(
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("npc_location").asJsonObject().getValue("world_name").asString(),
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("npc_location").asJsonObject().getValue("x").asDouble(),
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("npc_location").asJsonObject().getValue("y").asDouble(),
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("npc_location").asJsonObject().getValue("z").asDouble(),
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("npc_location").asJsonObject().getValue("yaw").asFloat(),
                                            jsonObject.getValue("server_settings").asJsonObject().getValue("npc_location").asJsonObject().getValue("pitch").asFloat(),
                                        )
                                    ),
                                    null
                                )
                            }
                        }
                    )
                }
            }

            override fun provide() = Any()

        }

    }

    object Repositories {

        object MariaDB {

            val SERVERS_CONFIGURATION_REPOSITORY = MariaDBRepositoryProvider<IServersConfigurationRepository>(
                MariaDBServersConfigurationRepository::class
            )

        }

    }

    object Cache {

        object Local {

            val SERVER_CONFIGURATION = LocalCacheProvider(
                ServersConfigurationsLocalCache()
            )

        }
    }

}