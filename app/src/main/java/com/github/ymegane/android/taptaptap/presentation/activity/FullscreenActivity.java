package com.github.ymegane.android.taptaptap.presentation.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.ymegane.android.taptaptap.R;
import com.github.ymegane.android.taptaptap.databinding.ActivityFullscreenBinding;
import com.github.ymegane.android.taptaptap.domain.usecase.SoundEffectUseCaseImpl;
import com.github.ymegane.android.taptaptap.presentation.presenter.TapPresenter;
import com.github.ymegane.android.taptaptap.presentation.presenter.TapPresenterImpl;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private TapPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityFullscreenBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_fullscreen);
        presenter = new TapPresenterImpl(this, binding, new SoundEffectUseCaseImpl(this));
        presenter.init();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        presenter.delayedHide(100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.delayedHide(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.release();
    }
}
