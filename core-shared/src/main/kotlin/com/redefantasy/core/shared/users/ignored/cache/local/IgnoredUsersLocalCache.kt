package com.redefantasy.core.shared.users.ignored.cache.local

import com.github.benmanes.caffeine.cache.Caffeine
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.cache.local.LocalCache
import com.redefantasy.core.shared.users.ignored.data.IgnoredUser
import com.redefantasy.core.shared.users.ignored.storage.dto.FetchIgnoredUsersByUserIdDTO
import com.redefantasy.core.shared.users.storage.table.UsersTable
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author SrGutyerrez
 **/
class IgnoredUsersLocalCache : LocalCache {

    private val CACHE = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build<EntityID<UUID>, List<IgnoredUser>> {
                CoreProvider.Repositories.Mongo.USERS_IGNORED_REPOSITORY.provide().fetchByUserId(
                        FetchIgnoredUsersByUserIdDTO(
                                it
                        )
                )
            }

    fun fetchByUserId(userId: UUID) = this.CACHE.get(
        EntityID(userId, UsersTable)
    )

    fun fetchByUserId(userId: EntityID<UUID>) = this.CACHE.get(userId)

    fun invalidate(userId: EntityID<UUID>) = this.CACHE.invalidate(userId)

}