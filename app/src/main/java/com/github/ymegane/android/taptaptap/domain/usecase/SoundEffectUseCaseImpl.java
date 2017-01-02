package com.github.ymegane.android.taptaptap.domain.usecase;

import android.content.Context;

import com.github.ymegane.android.taptaptap.data.repository.SoundEffectRepositoryImpl;
import com.github.ymegane.android.taptaptap.domain.repository.SoundEffectRepository;

public class SoundEffectUseCaseImpl implements SoundEffectUseCase {

    private final SoundEffectRepository repository;

    public SoundEffectUseCaseImpl(SoundEffectRepository repository) {
        this.repository = repository;
        this.repository.load();
    }

    public SoundEffectUseCaseImpl(Context context) {
        this.repository = new SoundEffectRepositoryImpl(context);
        this.repository.load();
    }


    @Override
    public void playRandom() {
        repository.play();
    }

    @Override
    public void stop() {
       repository.release();
    }
}
