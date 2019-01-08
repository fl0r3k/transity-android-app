package pl.transity.app.adapter.decorator


import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import pl.transity.app.utilities.DisplayUtils


class StopLinesItemDecorator(
        activity: Activity,
        leftMargin: Int,
        space: Int,
        rightMargin: Int
) : RecyclerView.ItemDecoration() {

    private val leftMarginPixels = DisplayUtils.dpToPixels(activity,leftMargin)
    private val spacePixels = DisplayUtils.dpToPixels(activity,space)
    private val rightMarginPixels = DisplayUtils.dpToPixels(activity,rightMargin)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)

        if (position == 0)
            outRect.left = leftMarginPixels
        else
            outRect.left = spacePixels

        if (position == parent.adapter!!.itemCount-1)
            outRect.right = rightMarginPixels
        else
            outRect.right = spacePixels
    }
}