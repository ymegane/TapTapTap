package com.github.ymegane.android.taptaptap.presentation.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import com.github.ymegane.android.taptaptap.R
import com.github.ymegane.android.taptaptap.domain.model.Circle

class CircleView(context: Context, val circle: Circle) : View(context) {
    private val paint = Paint()
    private var animator: ValueAnimator? = null
    private val defaultRadius: Float = resources.getDimensionPixelSize(R.dimen.circle_radius).toFloat()

    private val cancelHandler = Handler(Looper.getMainLooper())
    private val cancelTimer = Runnable { startAnimation() }

    init {
        initLayout()
    }

    private fun initLayout() {
        elevation = resources.getDimension(R.dimen.circle_elevation)

        val width = resources.getDimensionPixelSize(R.dimen.circle_width)
        val height = resources.getDimensionPixelSize(R.dimen.circle_width)
        val params = FrameLayout.LayoutParams(width, height)
        params.setMargins(circle.point.x - width / 2, circle.point.y - height / 2, 0, 0)
        layoutParams = params
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        cancelHandler.postDelayed(cancelTimer, DURATION.toLong())
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelHandler.removeCallbacks(cancelTimer)
    }

    fun continueCancelTimer() {
        cancelHandler.removeCallbacks(cancelTimer)
        cancelHandler.postDelayed(cancelTimer, DURATION.toLong())
    }

    fun startAnimation() {
        cancelHandler.removeCallbacks(cancelTimer)

        animator = ValueAnimator.ofFloat(defaultRadius, 0f)
        animator?.let {
            animator?.interpolator = LinearOutSlowInInterpolator()
            animator?.duration = DURATION.toLong()
            animator?.addUpdateListener { invalidate() }
            animator?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    if (!isAttachedToWindow) {
                        return
                    }
                    (parent as ViewGroup).removeView(this@CircleView)
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
            animator?.start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        var radius = defaultRadius
        animator?.let {
            radius = animator?.animatedValue as Float
        }
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)
        paint.alpha = Math.round(200 * (radius / defaultRadius))
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
    }

    fun equals(circle: Circle) = this.circle == circle

    companion object {
        private val DURATION = 3000
    }
}


