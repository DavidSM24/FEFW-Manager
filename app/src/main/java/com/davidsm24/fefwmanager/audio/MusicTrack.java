package com.davidsm24.fefwmanager.audio;

import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.route.Route;

public enum MusicTrack {
    MAIN_SCREEN(R.raw.music_main_screen),
    CAI(R.raw.music_cai),
    LEDA(R.raw.music_leda),
    DIETRICH(R.raw.music_dietrich),
    THEODORA(R.raw.music_theodora);

    @RawRes final int audioResource;

    MusicTrack(@RawRes int audioResource) {
        this.audioResource = audioResource;
    }

    public static MusicTrack forRoute(@Nullable Route route) {
        if (route == null) {
            return MAIN_SCREEN;
        }
        return switch (route) {
            case CAI -> CAI;
            case LEDA -> LEDA;
            case DIETRICH -> DIETRICH;
            case THEODORA -> THEODORA;
        };
    }
}
