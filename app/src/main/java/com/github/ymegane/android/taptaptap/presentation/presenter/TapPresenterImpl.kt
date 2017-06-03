package com.github.ymegane.android.taptaptap.presentation.presenter

import android.annotation.SuppressLint
import android.os.Handler
import android.view.View

import com.github.ymegane.android.taptaptap.databinding.ActivityFullscreenBinding
import com.github.ymegane.android.taptaptap.domain.usecase.SoundEffectUseCase
import com.github.ymegane.android.taptaptap.presentation.activity.FullscreenActivity
import com.github.ymegane.android.taptaptap.presentation.view.TapView
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class TapPresenterImpl(private val activity: FullscreenActivity, private val binding: ActivityFullscreenBinding, private val useCase: SoundEffectUseCase) : TapPresenter {
    private val mHideHandler = Handler()
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        val actionBar = activity.supportActionBar
        actionBar?.show()
        binding.dummyButton.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        binding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    override fun init() {
        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        binding.tapView.tapListener = object : TapView.OnTapListener {
            override fun onTap() {
                Observable.fromCallable({
                    useCase.playRandom()
                }).subscribeOn(Schedulers.io()).subscribe()
            }
        }
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    override fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    override fun release() {
        useCase.stop()
        binding.tapView.release()
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        val actionBar = activity.supportActionBar
        actionBar?.hide()
        binding.dummyButton.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @SuppressLint("InlinedApi")
    private fun show() {
        // Show the system bar
        binding.tapView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    companion object {

        /**
         * Whether or not the system UI should be auto-hidden after
         * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
