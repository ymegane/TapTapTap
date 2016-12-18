package com.github.ymegane.android.taptaptap.domain.usecase;

import android.content.Context;

import com.github.ymegane.android.taptaptap.data.repository.SoundEffectRepositoryImpl;
import com.github.ymegane.android.taptaptap.domain.repository.SoundEffectRepository;

public class SoundEffectUseCaseImpl implements SoundEffectUseCase {

    private final SoundEffectRepository mRepository;

    public SoundEffectUseCaseImpl(SoundEffectRepository repository) {
        mRepository = repository;
        mRepository.load();
    }

    public SoundEffectUseCaseImpl(Context context) {
        mRepository = new SoundEffectRepositoryImpl(context);
        mRepository.load();
    }


    @Override
    public void playRandom() {
        mRepository.play();
    }

    @Override
    public void stop() {
       mRepository.release();
    }
}
