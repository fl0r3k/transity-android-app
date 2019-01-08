package pl.transity.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.transity.app.data.model.FetchTimestamp


@Dao
interface FetchTimestampDao {

    @Query("SELECT * FROM fetch_timestamp WHERE id = :id")
    fun get(id : String) : FetchTimestamp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fetchTimestamp: FetchTimestamp)

    @Query("DELETE FROM stops")
    fun deleteAll()
}