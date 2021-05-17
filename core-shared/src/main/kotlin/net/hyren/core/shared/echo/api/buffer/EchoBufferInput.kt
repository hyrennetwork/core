package net.hyren.core.shared.echo.api.buffer

import com.google.common.base.Enums
import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteStreams
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.applications.ApplicationType
import net.hyren.core.shared.applications.data.Application
import net.hyren.core.shared.groups.Group
import net.hyren.core.shared.misc.json.KJson
import net.hyren.core.shared.servers.storage.table.ServersTable
import net.hyren.core.shared.world.location.SerializedLocation
import net.md_5.bungee.chat.ComponentSerializer
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import java.net.InetSocketAddress
import java.util.*
import kotlin.reflect.KClass

/**
 * @author SrGutyerrez
 **/
class EchoBufferInput(
    bytes: ByteArray
) {

    private val buffer: ByteArrayDataInput = ByteStreams.newDataInput(bytes)

    fun readBoolean() = this.buffer.readBoolean()

    fun readByte() = this.buffer.readByte()

    fun readUnsignedByte() = this.buffer.readUnsignedByte()

    fun readShort() = this.buffer.readShort()

    fun readUnsignedShort() = this.buffer.readUnsignedShort()

    fun readChar() = this.buffer.readChar()

    fun readInt(): Int? {
        val valid = this.readBoolean()

        if (valid) this.buffer.readInt()

        return null
    }

    fun readLong() = this.buffer.readLong()

    fun readDouble() = this.buffer.readDouble()

    fun readFloat(): Float? {
        val valid = this.readBoolean()

        if (valid) return this.buffer.readFloat()

        return null
    }

    fun readString(): String? {
        val valid = this.readBoolean()

        if (valid) return this.buffer.readUTF()

        return null
    }

    fun <T : Enum<T>> readEnum(clazz: KClass<T>, deft: T? = null): T? {
        val string = this.readString() ?: return null

        val optional = Enums.getIfPresent(clazz.java, string)

        if (deft !== null) return optional.or(deft)

        return optional.orNull()
    }

    fun readUUID(): UUID? {
        val valid = this.buffer.readBoolean()

        if (valid) {
            val mostSignificantBits = this.buffer.readLong()
            val leastSignificantBits = this.buffer.readLong()

            return UUID(mostSignificantBits, leastSignificantBits)
        }

        return null
    }

    @Deprecated("readEntityID(table: IdTable<T>) is deprecated")
    inline fun <reified T: Comparable<T>> readEntityID(
        table: IdTable<T> // ignore
    ): EntityID<T>? {
        val valid = this.readBoolean()

        if (valid) {
            val stringified = readString()

            println("Stringified: $stringified")

            return KJson.decodeFromString(stringified)
        }

        return null
    }

    inline fun <reified T: Comparable<T>> readEntityID(): EntityID<T>? {
        val valid = this.readBoolean()

        if (valid) {
            return KJson.decodeFromString(readString()!!)
        }

        return null
    }

    @Deprecated(
        "read address is deprecated",
        ReplaceWith("readAddressInetSocketAddress()"),
        DeprecationLevel.WARNING
    )
    fun readAddress() = this.readAddressInetSocketAddress()

    fun readAddressInetSocketAddress(): InetSocketAddress? {
        val value = this.readString() ?: return null

        if (value.startsWith("[")) {
            val i = value.lastIndexOf(']')

            if (i == -1) {
                return null
            }

            val j = value.indexOf(':', i)
            val port = if (j > -1) value.substring(j + 1).toInt() else 0

            return InetSocketAddress(value.substring(0, i + 1), port)
        } else {
            val i = value.indexOf(':')

            return if (i != -1 && value.indexOf(':', i + 1) == -1) {
                val port = value.substring(i + 1).toInt()

                InetSocketAddress(value.substring(0, i), port)
            } else {
                InetSocketAddress(value, 0)
            }
        }
    }

    fun readApplication(): Application? {
        val valid = this.buffer.readBoolean()

        if (valid) return Application(
            readString()!!,
            readString()!!,
            readInt(),
            readAddressInetSocketAddress()!!,
            readEnum(ApplicationType::class)!!,
            readServer(),
            readEnum(Group::class)
        )

        return null
    }

    fun readServer() = CoreProvider.Cache.Local.SERVERS.provide().fetchByName(readEntityID(ServersTable))

    fun readSerializedLocation() = SerializedLocation.of(readString())

    fun readBaseComponent() = ComponentSerializer.parse(readString())

    inline fun <reified T> readList(): List<T>? {
        val valid = readBoolean()

        if (!valid) return null

        return KJson.decodeFromString(readString()!!)
    }

    inline fun <reified T> readArray(): Array<T>? {
        val valid = readBoolean()

        if (!valid) return null

        return KJson.decodeFromString(readString()!!)
    }

    fun readJson() = KJson.encodeToJsonElement(readString())

}