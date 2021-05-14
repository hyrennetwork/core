package net.hyren.core.shared.misc.exposed

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import java.sql.SQLFeatureNotSupportedException
import kotlin.reflect.KClass

/**
 * @author Gutyerrez
 */
inline fun <reified T> Table.array(
    name: String
): Column<Array<T>> = registerColumn(name, ArrayColumnType<T>(T::class))

class ArrayColumnType<T>(
    private val kClass: KClass<*>
) : ColumnType() {

    override fun sqlType() = "longtext"

    @InternalSerializationApi
    @ExperimentalSerializationApi
    override fun valueFromDB(
        value: Any
    ): Any {
        if (value is java.sql.Array) {
            if (value.array === null) {
                error("Cannot read null array")
            }

            return value.array
        } else if (value is String) {
            return Json.decodeFromString(object : DeserializationStrategy<Array<T>> {
                override val descriptor: SerialDescriptor = ContextualSerializer(
                    kClass,
                    null,
                    emptyArray()
                ).descriptor

                override fun deserialize(
                    decoder: Decoder
                ): Array<T> {
                    val a = decoder.serializersModule.getContextual(kClass)?.deserialize(decoder)

                    println(a)

                    return java.lang.reflect.Array.newInstance(
                        kClass.java,
                        Int.MAX_VALUE
                    ) as Array<T>
                }
            }, value)
        } else if (value is Array<*>) {
            return value
        }

        throw SQLFeatureNotSupportedException("Array does not support for this database")
    }

    override fun setParameter(
        stmt: PreparedStatementApi, index: Int, value: Any?
    ) {
        super.setParameter(stmt, index, value.let {
            Json.encodeToString(it)
        })
    }
}
