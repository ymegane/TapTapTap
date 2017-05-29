package com.github.ymegane.android.taptaptap.domain.usecase

import android.content.Context

import com.github.ymegane.android.taptaptap.data.repository.SoundEffectRepositoryImpl
import com.github.ymegane.android.taptaptap.domain.repository.SoundEffectRepository

class SoundEffectUseCaseImpl : SoundEffectUseCase {

    private val repository: SoundEffectRepository

    @Suppress("unused")
    constructor(repository: SoundEffectRepository) {
        this.repository = repository
        this.repository.load()
    }

    constructor(context: Context) {
        this.repository = SoundEffectRepositoryImpl(context)
        this.repository.load()
    }

    override fun playRandom() = repository.play()

    override fun stop() = repository.release()
}
