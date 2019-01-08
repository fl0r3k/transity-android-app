package pl.transity.app.data.database

import androidx.room.TypeConverter
import java.sql.Timestamp

class TimestampConverter {

    @TypeConverter
    fun fromLong(timestamp: Long): Timestamp {
        return Timestamp(timestamp)
    }

    @TypeConverter
    fun toLong(timestamp: Timestamp): Long {
        return timestamp.time
    }

}