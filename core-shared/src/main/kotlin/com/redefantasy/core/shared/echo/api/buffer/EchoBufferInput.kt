package com.redefantasy.core.shared.echo.api.buffer

import com.google.common.base.Enums
import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteStreams
import com.google.gson.JsonParser
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.applications.ApplicationType
import com.redefantasy.core.shared.applications.data.Application
import com.redefantasy.core.shared.groups.Group
import com.redefantasy.core.shared.servers.data.Server
import com.redefantasy.core.shared.world.location.SerializedLocation
import net.md_5.bungee.chat.ComponentSerializer
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.io.Serializable
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

    fun readByteArray(): ByteArray {
        val bytes = mutableListOf<Byte>()

        do {
            val byte = this.buffer.readByte()

            println(byte)

            bytes.add(byte)
        } while (this.readBoolean())

        return bytes.toByteArray()
    }

    fun readAddress(): InetSocketAddress? {
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
                this.readString()!!,
                this.readString()!!,
                this.readString(),
                this.readInt(),
                this.readAddress()!!,
                this.readEnum(ApplicationType::class)!!,
                this.readServer(),
                this.readEnum(Group::class)
        )

        return null
    }

    fun readServer(): Server? {
        val serverName = this.readString() ?: return null

        return CoreProvider.Cache.Local.SERVERS.provide().fetchByName(serverName)
    }

    fun readSerializedLocation() = SerializedLocation.of(this.readString())

    fun readBaseComponent() = ComponentSerializer.parse(this.readString())

    fun <T: Serializable> readList(): List<T>? {
        val list = mutableListOf<T>()

        val byteArray = this.readByteArray()

        println(byteArray)

        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val objectOutputStream = ObjectInputStream(byteArrayInputStream)

        do {
            val valid = this.readBoolean()
            val `object` = objectOutputStream.readObject() as T

            list.add(`object`)
        } while (valid)

        return list
    }

    fun readJsonObject() = JsonParser.parseString(this.readString()).asJsonObject

}