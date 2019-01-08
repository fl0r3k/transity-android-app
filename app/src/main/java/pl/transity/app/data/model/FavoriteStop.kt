package pl.transity.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favourite_stops")
data class FavoriteStop(
        @PrimaryKey val id: String
)