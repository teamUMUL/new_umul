package inu.thebite.umul.room.chart

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChartDao {
    @Insert
    fun addChart(chartEntity : ChartEntity)


    @Query("SELECT * FROM `chart`")
    fun getAllCharts(): Flow<List<ChartEntity>>

    @Delete
    fun deleteChart(chartEntity :ChartEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateChart(chartEntity : ChartEntity)
}