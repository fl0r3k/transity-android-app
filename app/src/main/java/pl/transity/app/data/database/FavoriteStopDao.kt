package pl.transity.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import pl.transity.app.data.model.FavoriteStop
import io.reactivex.Flowable


@Dao
interface FavoriteStopDao {

//    @Query("SELECT id FROM favourite_stops")
//    fun getFavoriteStopsIds() : LiveData<List<String>>

    @Query("SELECT id FROM favourite_stops")
    fun getFavoriteStopsIds() : Flowable<List<String>>

    @Insert(onConflict = REPLACE)
    fun addFavoriteStop(favoriteStop : FavoriteStop)

    @Insert(onConflict = REPLACE)
    fun addFavoriteStops(lines: List<FavoriteStop>)

    @Query("DELETE FROM favourite_stops WHERE id = :id")
    fun removeFavoriteStop(id : String)

    @Query("DELETE FROM favourite_stops")
    fun removeAll()
}