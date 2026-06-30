package com.devindie.myday.data.source.local.browse

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [BrowseCardEntity::class, BrowseRemoteKeyEntity::class],
    version = 1,
)
@ConstructedBy(BrowseDatabaseConstructor::class)
abstract class BrowseDatabase : RoomDatabase() {
    abstract fun browseCardDao(): BrowseCardDao

    abstract fun browseRemoteKeyDao(): BrowseRemoteKeyDao
}

expect object BrowseDatabaseConstructor : RoomDatabaseConstructor<BrowseDatabase> {
    override fun initialize(): BrowseDatabase
}
