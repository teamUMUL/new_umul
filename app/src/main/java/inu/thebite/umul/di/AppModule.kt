package inu.thebite.umul.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import inu.thebite.umul.room.chart.ChartDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Provides
    @Singleton
    fun provideChartDb(app: Application): ChartDatabase{
        return Room
            .databaseBuilder(app, ChartDatabase::class.java, "chart db")
                .fallbackToDestructiveMigration()
                .build()
    }
}