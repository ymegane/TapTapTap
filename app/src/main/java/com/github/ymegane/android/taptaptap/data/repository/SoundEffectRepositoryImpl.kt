package com.github.ymegane.android.taptaptap.data.repository

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

import com.github.ymegane.android.taptaptap.R
import com.github.ymegane.android.taptaptap.domain.repository.SoundEffectRepository

import java.security.SecureRandom
import java.util.ArrayList

class SoundEffectRepositoryImpl(private val context: Context) : SoundEffectRepository {
    private var soundPool: SoundPool? = null
    private val loadedSounds = ArrayList<Int>()
    private val secureRandom: SecureRandom = SecureRandom()

    private val randomEffectId: Int
        get() {
            if (loadedSounds.size != SOUNDS.size) {
                return -1
            }
            return loadedSounds[secureRandom.nextInt(SOUNDS.size)]
        }

    private fun havingCount(): Int {
        return SOUNDS.size
    }

    override fun load() {
        soundPool = SoundPool.Builder()
                .setMaxStreams(havingCount())
                .setAudioAttributes(AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build())
                .build()
        load(0)
    }

    override fun play() {
        soundPool?.play(randomEffectId, 1f, 1f, 0, 0, 1f)
    }

    override fun release() {
        soundPool?.release()
    }

    private fun load(i: Int) {
        soundPool?.setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener { soundPool, sampleId, status ->
            Log.v(javaClass.simpleName, "sampleId=$sampleId status=$status")
            if (i == SOUNDS.size - 1) {
                return@OnLoadCompleteListener
            }
            load(i + 1)
        })

        soundPool?.let {
            val id = soundPool?.load(context, SOUNDS[i], 1) as Int
            loadedSounds.add(id)
        }
    }

    companion object {
        private val SOUNDS = intArrayOf(R.raw.ani_ge_chicken_koke03, R.raw.ani_ge_dog_wan01, R.raw.ani_ge_horse01, R.raw.ani_ge_owl02, R.raw.ani_ge_ushi02)
    }
}
