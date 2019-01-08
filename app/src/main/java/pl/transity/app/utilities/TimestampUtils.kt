package pl.transity.app.utilities

import java.sql.Timestamp

object TimestampUtils {


    fun diff(timestamp: Timestamp): Long {
        val ctm = System.currentTimeMillis()
        return timestamp.time - ctm
    }

    @JvmStatic
    fun diffMin(timestamp: Timestamp): Int {
        val diff = diff(timestamp)
        return Math.ceil(diff/1000.0/60.0).toInt()
    }

    fun autoFormat(millis: Long): String {
        return if (millis < 60000) {
            toSecondsString(millis)
        } else {
            toMinutesString(millis)
        }
    }

    private fun toMinutesString(millis : Long): String{
        val minutes = (millis / 1000 / 60).toInt()
        val suffix = if (minutes == 1) "minuta" else if (minutes in 2..4) "minuty" else "minut"
        return "$minutes $suffix"
    }

    fun toSecondsString(millis : Long): String{
        val seconds = (millis / 1000).toInt()
        val suffix = if (seconds == 1) "sekunda" else if (seconds in 2..4) "sekundy" else "sekund"
        return "$seconds $suffix"
    }
}