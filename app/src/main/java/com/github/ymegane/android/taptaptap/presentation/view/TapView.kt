package com.github.ymegane.android.taptaptap.presentation.view

import android.content.Context
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.github.ymegane.android.taptaptap.domain.model.Circle

import com.jakewharton.rxbinding.view.RxView

import java.util.ArrayList

import rx.Observable
import rx.Subscription

class TapView : FrameLayout {

    var tapListener: OnTapListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        prepareForTouchEvent()
    }

    private var lastVibratedTime: Long = 0
    private var subscription: Subscription? = null
    private fun prepareForTouchEvent() {
        subscription = RxView.touches(this).flatMap { event ->
            val n = event.pointerCount

            // Add circles for the number of points
            val circles = ArrayList<Circle>(n)
            (0..n - 1).mapTo(circles) { Circle(it, event) }
            Observable.from(circles)
        }.doOnNext { circle ->
            val event = circle.event
            if (event.action == MotionEvent.ACTION_MOVE) {
                // If ViewGroup already has circles, continue to display
                if (event.eventTime - lastVibratedTime > 600) {
                    lastVibratedTime = event.eventTime
                    vibrate()
                }
                val view = findCircleView(circle)
                view?.continueCancelTimer()
            } else if (event.actionMasked == MotionEvent.ACTION_UP
                    || event.actionMasked == MotionEvent.ACTION_POINTER_UP
                    || event.actionMasked == MotionEvent.ACTION_CANCEL) {
                // If ViewGroup already has circles, start hiding animation
                val view = findCircleView(circle)
                view?.startAnimation()
            }
        }.filter { circle ->
            // Filter down event
            val event = circle.event
            event.actionMasked == MotionEvent.ACTION_DOWN || event.actionMasked == MotionEvent.ACTION_POINTER_DOWN
        }.filter { circle -> findCircleView(circle) == null }
        .filter { childCount < 100 }
        .subscribe { circle ->
            addCircleView(circle)
            vibratePattern()
            tapListener?.onTap()
        }
    }

    fun release() = subscription?.unsubscribe()

    private fun findCircleView(circle: Circle): CircleView? {
        val n = childCount
        return (0..n - 1)
                .map { getChildAt(it) as CircleView }
                .firstOrNull { it.equals(circle) }
    }

    private fun addCircleView(circle: Circle) {
        val view = CircleView(context, circle)
        addView(view)
        view.invalidate()
    }

    private fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (!vibrator.hasVibrator()) {
            return
        }

        vibrator.vibrate(500)
    }

    private fun vibratePattern() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (!vibrator.hasVibrator()) {
            return
        }

        vibrator.vibrate(PATTERN, 1)
    }

    interface OnTapListener {
        fun onTap()
    }

    companion object {
        private val PATTERN = longArrayOf(100, 500)
    }
}
