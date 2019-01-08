package pl.transity.app.utilities

import android.view.View

object PaddingUtils{

    fun setPaddingBottom(view: View, paddingBottom: Int) {
        val paddingLeft = view.paddingLeft
        val paddingTop = view.paddingTop
        val paddingRight = view.paddingRight
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    }

    fun setPaddingTop(view: View, paddingTop: Int) {
        val paddingLeft = view.paddingLeft
        val paddingRight = view.paddingRight
        val paddingBottom = view.paddingBottom
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    }
}
