package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    ScanRecordEntity::class, 
    FarmCropEntity::class,
    SensorReadingEntity::class,
    DeviceEntity::class,
    MarketPriceEntity::class,
    WeatherObservationEntity::class
  ],
  version = 3,
  exportSchema = false
)
abstract class KisanDatabase : RoomDatabase() {
  abstract fun kisanDao(): KisanDao

  companion object {
    @Volatile
    private var INSTANCE: KisanDatabase? = null

    fun getDatabase(context: Context): KisanDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          KisanDatabase::class.java,
          "kisan_ai_database"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
