package com.github.ymegane.android.taptaptap.presentation.activity

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.github.ymegane.android.taptaptap.R
import com.github.ymegane.android.taptaptap.databinding.ActivityFullscreenBinding
import com.github.ymegane.android.taptaptap.domain.usecase.SoundEffectUseCaseImpl
import com.github.ymegane.android.taptaptap.presentation.presenter.TapPresenter
import com.github.ymegane.android.taptaptap.presentation.presenter.TapPresenterImpl

class FullscreenActivity : AppCompatActivity() {

    private var presenter: TapPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityFullscreenBinding>(this, R.layout.activity_fullscreen)
        presenter = TapPresenterImpl(this, binding, SoundEffectUseCaseImpl(this))
        presenter?.init()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        presenter?.delayedHide(100)
    }

    override fun onResume() {
        super.onResume()
        presenter?.delayedHide(100)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.release()
    }
}
