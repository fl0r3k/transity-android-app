package pl.transity.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp


@Entity(tableName = "fetch_timestamp")
data class FetchTimestamp(
        @PrimaryKey
        val id: String,
        val timestamp: Timestamp
){

    override fun toString(): String {
        return "($id,$timestamp)"
    }
}