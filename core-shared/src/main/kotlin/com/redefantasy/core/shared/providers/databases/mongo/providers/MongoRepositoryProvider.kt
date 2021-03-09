package com.redefantasy.core.shared.providers.databases.mongo.providers

import com.redefantasy.core.shared.providers.IProvider
import com.redefantasy.core.shared.providers.databases.mongo.MongoDatabaseProvider
import com.redefantasy.core.shared.storage.repositories.IRepository
import kotlin.reflect.KClass

/**
 * @author SrGutyerrez
 **/
class MongoRepositoryProvider<T : IRepository>(
    private val mongoDatabaseProvider: MongoDatabaseProvider,
    private val repositoryClass: KClass<out T>
) : IProvider<T> {

    private lateinit var t: T

    override fun prepare() {
        this.t = this.repositoryClass.constructors
            .first()
            .call(this.mongoDatabaseProvider)
    }

    override fun provide() = this.t
}