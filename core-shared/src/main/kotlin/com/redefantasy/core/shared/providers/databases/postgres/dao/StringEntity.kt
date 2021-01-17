package com.redefantasy.core.shared.providers.databases.postgres.dao

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

/**
 * @author SrGutyerrez
 **/
abstract class StringEntity(name: EntityID<String>) : Entity<String>(name)

abstract class StringEntityClass<out E : StringEntity>(
        table: IdTable<String>,
        entityType: Class<E>? = null
) : EntityClass<String, E>(table, entityType)