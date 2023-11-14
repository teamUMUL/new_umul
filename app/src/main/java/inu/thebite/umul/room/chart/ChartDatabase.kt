package inu.thebite.umul.room.chart

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChartEntity::class], version = 2)
abstract class ChartDatabase : RoomDatabase() {
    abstract val chartDao: ChartDao
}