package com.example.communityapp.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

fun moveAndResizeView(view: View, translationY: Float, newHeight: Int) {
    // Translation animation
    val translationAnimator = ObjectAnimator.ofFloat(view, "translationY", translationY).apply {
        duration = 500
    }

    // Height adjustment animation
    val currentHeight = view.height
    val heightAnimator = ValueAnimator.ofInt(currentHeight, newHeight).apply {
        duration = 500

        addUpdateListener {
            val value = it.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }
    }

    // Create an AnimatorSet to play both animations together
    val animationSet = AnimatorSet().apply {
        interpolator = AccelerateDecelerateInterpolator()
        playTogether(translationAnimator, heightAnimator)
    }

    // Start the animation set
    animationSet.start()
}
