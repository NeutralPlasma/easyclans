package eu.virtusdevelops.easyclans.core.database

import eu.virtusdevelops.easyclans.api.Success

interface DaoCrud <T, ID> {

    fun init(): Result<Success>

    fun getById(id: ID): Result<T>

    fun getAll(): Result<List<T>>

    fun save(t: T): Result<Success>

    fun update(t: T): Result<Success>

    fun deleteById(id: ID): Result<Success>
}