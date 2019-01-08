package pl.transity.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.transity.app.data.model.FavoriteLine
import pl.transity.app.data.model.FavoriteStop
import pl.transity.app.data.model.FetchTimestamp
import pl.transity.app.data.model.Stop
import pl.transity.app.data.model.StopsGroup

@Database(entities = [Stop::class, StopsGroup::class, FetchTimestamp::class, FavoriteLine::class, FavoriteStop::class], version = 1, exportSchema = false)
@TypeConverters(TimestampConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stopDao(): StopDao
    abstract fun stopsGroupDao(): StopsGroupDao
    abstract fun fetchTimestampDao(): FetchTimestampDao
    abstract fun favouriteLineDao(): FavoriteLineDao
    abstract fun favoriteStopDao(): FavoriteStopDao

    companion object {

        private val TAG = AppDatabase::class.simpleName
        private const val DATABASE_NAME = "app"
        private var INSTANCE: AppDatabase? = null


        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, DATABASE_NAME)
                        .build()
    }
}
