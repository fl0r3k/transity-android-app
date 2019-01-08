package pl.transity.app.utilities

import android.app.Activity
import android.util.DisplayMetrics


object DisplayUtils {

    private fun getDisplayMetrics(activity: Activity) : DisplayMetrics {
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        return dm
    }

    fun getDensity(activity: Activity) : Float {
        return getDisplayMetrics(activity).density
    }

    fun getHeightInPixels(activity: Activity): Int {
        return getDisplayMetrics(activity).heightPixels
    }

    fun getHeightInDp(activity: Activity): Int {
        return dpToPixels(activity,getHeightInPixels(activity))
    }

    fun getWidthInPixels(activity: Activity): Int {
        return getDisplayMetrics(activity).widthPixels
    }

    fun getWidthInDp(activity: Activity): Int {
        return dpToPixels(activity,getWidthInPixels(activity))
    }

    fun dpToPixels(activity: Activity, dp: Int): Int {
        return (dp * getDensity(activity)).toInt()
    }
}