package pl.transity.app.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import pl.transity.app.data.model.StopsGroup

@Dao
interface StopsGroupDao {

    @Query("SELECT * FROM stops_groups")
    fun getStopsGroups() : LiveData<List<StopsGroup>>

    @Query("SELECT * FROM stops_groups WHERE lat >= :s AND lat <= :n AND lon >= :w AND lon <= :e")
    fun getStopsGroupsInBounds(n: Double, s: Double, w: Double, e: Double): List<StopsGroup>

    @Insert(onConflict = REPLACE)
    fun bulkInsert(stopsGroups: List<StopsGroup>)

    @Query("DELETE FROM stops_groups")
    fun deleteAll()
}
