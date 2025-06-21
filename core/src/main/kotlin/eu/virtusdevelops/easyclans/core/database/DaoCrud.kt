package eu.virtusdevelops.easyclans.core.database

import eu.virtusdevelops.easyclans.api.Success

interface DaoCrud <T, ID> {

    suspend fun init(): Result<Success>

    suspend fun getById(id: ID): Result<T>

    suspend fun getAll(): Result<List<T>>

    suspend fun save(t: T): Result<Success>

    suspend fun deleteById(id: ID): Result<Success>
}