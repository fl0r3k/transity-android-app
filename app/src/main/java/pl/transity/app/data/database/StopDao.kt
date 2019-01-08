package pl.transity.app.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import pl.transity.app.data.model.Stop

@Dao
interface StopDao {

    @Query("SELECT * FROM stops WHERE id = :id")
    fun getStopById(id: String): Stop

    @Query("SELECT s.* FROM stops AS s INNER JOIN favourite_stops AS fs WHERE s.id = fs.id")
    fun getFavoriteStops(): LiveData<List<Stop>>

    @Query("SELECT * FROM stops WHERE lat >= :s AND lat <= :n AND lon >= :w AND lon <= :e")
    fun getStopsInBounds(n: Double, s: Double, w: Double, e: Double): List<Stop>

    @Query("SELECT * FROM stops")
    fun getStops(): LiveData<List<Stop>>

    @Insert(onConflict = REPLACE)
    fun bulkInsert(stops: List<Stop>)

    @Query("DELETE FROM stops")
    fun deleteAll()
}