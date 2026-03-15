package com.mleval.pexelsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CollectionDbModel::class, PhotoDbModel::class, BookMarkDbModel::class],
    version = 4,
    exportSchema = false
)
abstract class PexelsDataBase: RoomDatabase() {

    abstract fun dao(): PexelsDao

}
