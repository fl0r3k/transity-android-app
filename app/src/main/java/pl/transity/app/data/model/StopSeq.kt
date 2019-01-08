package pl.transity.app.data.model

import com.google.gson.annotations.SerializedName

class StopSeq(
        @SerializedName("id") val stopId: String,
        @SerializedName("sequence") val seq: Int
)