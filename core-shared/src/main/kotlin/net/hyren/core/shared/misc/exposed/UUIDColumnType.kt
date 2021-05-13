package net.hyren.core.shared.misc.exposed

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.nio.ByteBuffer
import java.util.*

/**
 * @author Gutyerrez
 */
fun Table.uuid4(
    columnName: String
): Column<UUID> = registerColumn(columnName, UUIDColumnType())

class UUIDColumnType : ColumnType() {

    override fun sqlType(): String = currentDialect.dataTypeProvider.uuidType()

    override fun valueFromDB(value: Any): UUID {
        println(">> " + value::class.qualifiedName)

        return when {
            value is UUID -> value
            value is ByteArray -> {
                // Start Check if is uuid
                println(value.size)

                if (value.size >= 36) {
                    println("ByteArray: $value/${value.size}")

                    val possibleUUID = String(value)

                    println("Value from ByteArray: $possibleUUID")

                    if (possibleUUID.matches(uuidRegexp)) {
                        return UUID.fromString(possibleUUID)
                    }
                }
                // End Check

                ByteBuffer.wrap(value).let { b -> UUID(b.long, b.long) }
            }
            value is String && value.matches(uuidRegexp) -> UUID.fromString(value)
            value is String -> ByteBuffer.wrap(value.toByteArray()).let { b -> UUID(b.long, b.long) }
            else -> error("Unexpected value of type UUID: $value of ${value::class.qualifiedName}")
        }
    }

    override fun notNullValueToDB(value: Any): Any = currentDialect.dataTypeProvider.uuidToDB(valueToUUID(value))

    override fun nonNullValueToString(value: Any): String = "'${valueToUUID(value)}'"

    private fun valueToUUID(value: Any): UUID {
        println("> " + value::class.qualifiedName)

        return when (value) {
            is UUID -> value
            is String -> UUID.fromString(value)
            is ByteArray -> ByteBuffer.wrap(value).let { UUID(it.long, it.long) }
            else -> error("Unexpected value of type UUID: ${value.javaClass.canonicalName}")
        }
    }

    companion object {
        private val uuidRegexp = Regex("[0-9A-F]{8}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12}", RegexOption.IGNORE_CASE)
    }

}