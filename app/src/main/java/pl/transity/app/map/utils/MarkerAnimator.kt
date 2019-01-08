package pl.transity.app.map.utils

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.util.Property
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import android.animation.Animator


object MarkerAnimator {

    fun fadeIn(marker: Marker) {
        ObjectAnimator
                .ofFloat(marker, "alpha", 0f, 1f)
                .setDuration(500)
                .start()
    }

    fun fadeOut(marker: Marker) {
        ObjectAnimator
                .ofFloat(marker, "alpha", 1f, 0f)
                .setDuration(500)
                .start()
    }

    fun fadeOutAndRemove(marker: Marker) {
        val animator = ObjectAnimator
                .ofFloat(marker, "alpha", 1f, 0f)
                .setDuration(500)

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animator: Animator) {
                marker.remove()
            }

            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        animator.start()
    }


    fun animateMarkerPosition(marker: Marker, finalPosition: LatLng, latLngInterpolator: LatLngInterpolator) {
        val typeEvaluator = TypeEvaluator<LatLng> { fraction, startValue, endValue -> latLngInterpolator.interpolate(fraction, startValue, endValue) }
        val property = Property.of(Marker::class.java, LatLng::class.java, "position")
        ObjectAnimator
                .ofObject<Marker, LatLng>(marker, property, typeEvaluator, finalPosition)
                .setDuration(1000)
                .start()
    }

    fun animateMarkerRotation(marker: Marker, finalRotation: Float) {
        val diff = Math.abs(finalRotation - marker.rotation)
//        val targetRotation = if (diff > 180) finalRotation + 360 else finalRotation
        ObjectAnimator
                .ofFloat(marker, "rotation", marker.rotation, finalRotation)
                .setDuration(500)
                .start()
    }
}