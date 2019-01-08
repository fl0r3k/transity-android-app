package pl.transity.app.adapter.decorator

import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import pl.transity.app.utilities.DisplayUtils


class GridItemDecorator(
        activity: Activity,
        spaceDp: Int,
        private val spanCount: Int
) : RecyclerView.ItemDecoration() {

    private val spacePixels = DisplayUtils.dpToPixels(activity, spaceDp)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        outRect.top = spacePixels
        outRect.right = spacePixels
        if (position % spanCount == 0)
            outRect.left = spacePixels
    }

}