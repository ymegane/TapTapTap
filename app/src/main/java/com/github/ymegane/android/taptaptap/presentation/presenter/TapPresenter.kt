package com.github.ymegane.android.taptaptap.presentation.presenter

interface TapPresenter {
    fun init()
    fun delayedHide(delayMillis: Int)
    fun release()
}
