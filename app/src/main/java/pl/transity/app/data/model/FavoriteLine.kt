package pl.transity.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favourite_lines")
data class FavoriteLine(
        @PrimaryKey val line: String
)