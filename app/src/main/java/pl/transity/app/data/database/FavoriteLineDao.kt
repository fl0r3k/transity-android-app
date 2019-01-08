package pl.transity.app.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import pl.transity.app.data.model.FavoriteLine
import io.reactivex.Flowable


@Dao
interface FavoriteLineDao {

//    @Query("SELECT line FROM favourite_lines")
//    fun getFavoriteLinesFlowable() : LiveData<List<String>>

    @Query("SELECT line FROM favourite_lines")
    fun getFavoriteLines() : Flowable<List<String>>

    @Query("SELECT * FROM favourite_lines")
    fun getFavoriteLinesLiveData() : LiveData<List<FavoriteLine>>


    @Insert(onConflict = REPLACE)
    fun addLine(line : FavoriteLine)

    @Insert(onConflict = REPLACE)
    fun addLines(lines: List<FavoriteLine>)

    @Query("DELETE FROM favourite_lines WHERE line = :line")
    fun removeLine(line : String)

    @Query("DELETE FROM favourite_lines")
    fun removeAll()
}