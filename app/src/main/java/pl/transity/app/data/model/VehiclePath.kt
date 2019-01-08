package pl.transity.app.data.model

import com.google.gson.annotations.Expose


class VehiclePath(
        @Expose
        val type : String,
        @Expose
        val line: String,
        @Expose
        val brigade: String,
        @Expose
        val count: Int,
        @Expose
        val points : List<PathPoint>
) {
    companion object {
        const val TRAM = 0
        const val BUS = 3
    }

    override fun toString(): String {
        return "($type,$line,$brigade,$count,$points)"
    }
}
