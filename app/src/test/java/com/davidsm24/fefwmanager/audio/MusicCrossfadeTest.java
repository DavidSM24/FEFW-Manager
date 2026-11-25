package com.davidsm24.fefwmanager.audio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class MusicCrossfadeTest {
    private static final float TOLERANCE = 0.0001f;
    private static final long START = 1_000;

    @Test
    public void usesEqualPowerAndFinishesWithOnlyTheTargetAudible() {
        MusicCrossfade fade = new MusicCrossfade();
        fade.setSolo(MusicTrack.MAIN_SCREEN);
        fade.begin(START);

        assertTrue(fade.advance(MusicTrack.CAI, START + MusicCrossfade.DURATION_MILLIS / 2));
        float midpoint = (float) Math.sqrt(0.5);
        assertEquals(midpoint, fade.volume(MusicTrack.MAIN_SCREEN), TOLERANCE);
        assertEquals(midpoint, fade.volume(MusicTrack.CAI), TOLERANCE);
        assertEquals(0f, fade.volume(MusicTrack.LEDA), 0f);

        assertFalse(fade.advance(MusicTrack.CAI, START + MusicCrossfade.DURATION_MILLIS));
        assertSolo(fade, MusicTrack.CAI);
    }

    @Test
    public void retargetsFromTheAudibleMixWhenInterrupted() {
        MusicCrossfade fade = new MusicCrossfade();
        fade.setSolo(MusicTrack.MAIN_SCREEN);
        fade.begin(START);

        long interrupt = START + MusicCrossfade.DURATION_MILLIS / 3;
        fade.advance(MusicTrack.CAI, interrupt);
        float menuVolume = fade.volume(MusicTrack.MAIN_SCREEN);
        float caiVolume = fade.volume(MusicTrack.CAI);

        fade.begin(interrupt);
        fade.advance(MusicTrack.THEODORA, interrupt);
        assertEquals(menuVolume, fade.volume(MusicTrack.MAIN_SCREEN), 0f);
        assertEquals(caiVolume, fade.volume(MusicTrack.CAI), 0f);
        assertEquals(0f, fade.volume(MusicTrack.THEODORA), 0f);

        fade.advance(MusicTrack.THEODORA, interrupt + MusicCrossfade.DURATION_MILLIS / 2);
        assertTrue(fade.volume(MusicTrack.MAIN_SCREEN) < menuVolume);
        assertTrue(fade.volume(MusicTrack.CAI) < caiVolume);
        assertTrue(fade.volume(MusicTrack.THEODORA) > 0f);

        assertFalse(fade.advance(MusicTrack.THEODORA, interrupt + MusicCrossfade.DURATION_MILLIS));
        assertSolo(fade, MusicTrack.THEODORA);
    }

    private static void assertSolo(MusicCrossfade fade, MusicTrack solo) {
        for (MusicTrack track : MusicTrack.values()) {
            assertEquals(track == solo ? 1f : 0f, fade.volume(track), 0f);
        }
    }
}
