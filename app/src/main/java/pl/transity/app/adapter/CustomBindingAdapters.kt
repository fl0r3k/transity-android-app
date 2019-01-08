package pl.transity.app.adapter

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.databinding.BindingAdapter

object CustomBindingAdapters {

    @JvmStatic
    @BindingAdapter("android:paddingLeft")
    fun setPaddingLeft(view: View, padding: Int) {
        view.setPadding(padding,
                view.paddingTop,
                view.paddingRight,
                view.paddingBottom)
    }

    @JvmStatic
    @BindingAdapter("backgroundColor")
    fun setBackgroundColor(view: View, color: Int) {
        val gd = view.background as GradientDrawable
        gd.setColor(color)
    }

}