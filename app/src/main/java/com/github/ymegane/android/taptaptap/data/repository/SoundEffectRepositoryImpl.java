package com.github.ymegane.android.taptaptap.data.repository;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import com.github.ymegane.android.taptaptap.R;
import com.github.ymegane.android.taptaptap.domain.repository.SoundEffectRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class SoundEffectRepositoryImpl implements SoundEffectRepository {

    private static final int[] SOUNDS = new int[]{
            R.raw.ani_ge_chicken_koke03,
            R.raw.ani_ge_dog_wan01,
            R.raw.ani_ge_horse01,
            R.raw.ani_ge_owl02,
            R.raw.ani_ge_ushi02
    };

    private Context mContext;
    private SoundPool mSoundPool;
    private final List<Integer> loadedSounds = new ArrayList<>();

    public SoundEffectRepositoryImpl(Context context) {
        mContext = context;
    }

    private int getRandomEffectId() {
        if (loadedSounds.size() != SOUNDS.length) {
            return -1;
        }
        return loadedSounds.get(new SecureRandom().nextInt(SOUNDS.length));
    }

    private int havingCount() {
        return SOUNDS.length;
    }

    @Override
    public void load() {
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(havingCount())
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build())
                .build();
        load(0);
    }

    @Override
    public void play() {
        mSoundPool.play(getRandomEffectId(), 1.f, 1.f, 0, 0, 1.f);
    }

    @Override
    public void release() {
        mSoundPool.release();
    }

    private void load(final int i) {
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(final SoundPool soundPool, int sampleId, int status) {
                Log.v(getClass().getSimpleName(), "sampleId=" + sampleId + " status=" + status);
                if (i == SOUNDS.length-1) {
                    return;
                }
                load(i + 1);
            }
        });
        int id = mSoundPool.load(mContext, SOUNDS[i], 1);
        loadedSounds.add(id);
    }
}
