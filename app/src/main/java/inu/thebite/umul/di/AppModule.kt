package inu.thebite.umul.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import inu.thebite.umul.bluetooth.data.AndroidBluetoothController
import inu.thebite.umul.bluetooth.domain.BluetoothController
import inu.thebite.umul.room.chart.ChartDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothController {
        return AndroidBluetoothController(context)
    }

    @Provides
    @Singleton
    fun provideChartDb(app: Application): ChartDatabase{
        return Room.databaseBuilder(
            app,
            ChartDatabase::class.java,
            "chart db"
        ).build()
    }
}