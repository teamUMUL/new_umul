package inu.thebite.umul.room.chart

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chart")
data class ChartEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    @ColumnInfo("date")
    val date : String,
    @ColumnInfo("gameMode")
    val gameMode : String,
    @ColumnInfo("chewCount")
    val chewCount : Float,
    @ColumnInfo("time")
    val time : Float,
)
