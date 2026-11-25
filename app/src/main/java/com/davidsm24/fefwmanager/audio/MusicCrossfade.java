package com.davidsm24.fefwmanager.audio;

import java.util.Arrays;

final class MusicCrossfade {
    static final long DURATION_MILLIS = 850;

    private final float[] weights = new float[MusicTrack.values().length];
    private final float[] initialWeights = new float[weights.length];
    private long startedAt;

    void setSolo(MusicTrack track) {
        Arrays.fill(weights, 0f);
        weights[track.ordinal()] = 1f;
    }

    void begin(long now) {
        System.arraycopy(weights, 0, initialWeights, 0, weights.length);
        startedAt = now;
    }

    boolean advance(MusicTrack target, long now) {
        float progress = Math.min(1f, (float) (now - startedAt) / DURATION_MILLIS);
        for (int index = 0; index < weights.length; index++) {
            float start = initialWeights[index];
            float end = index == target.ordinal() ? 1f : 0f;
            weights[index] = start + (end - start) * progress;
        }
        return progress < 1f;
    }

    float volume(MusicTrack track) {
        return (float) Math.sin(weights[track.ordinal()] * Math.PI / 2);
    }
}
