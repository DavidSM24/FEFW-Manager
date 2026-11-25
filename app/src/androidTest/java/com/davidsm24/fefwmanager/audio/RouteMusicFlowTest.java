package com.davidsm24.fefwmanager.audio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import android.content.Context;
import android.media.AudioManager;
import android.os.SystemClock;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.home.MainActivity;
import com.davidsm24.fefwmanager.route.Route;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class RouteMusicFlowTest {
    private static final String PACKAGE = "com.davidsm24.fefwmanager";
    private static final long TIMEOUT = 8_000;
    private final UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    @Test
    public void selectsTheBundledSoundtrackForEachScreen() {
        assertEquals(R.raw.music_main_screen, MusicTrack.forRoute(null).audioResource);
        assertEquals(R.raw.music_cai, MusicTrack.forRoute(Route.CAI).audioResource);
        assertEquals(R.raw.music_dietrich, MusicTrack.forRoute(Route.DIETRICH).audioResource);
        assertEquals(R.raw.music_leda, MusicTrack.forRoute(Route.LEDA).audioResource);
        assertEquals(R.raw.music_theodora, MusicTrack.forRoute(Route.THEODORA).audioResource);
    }

    @Test
    public void playsOnTheMainMenuAndEachRouteIncludingItsSubscreens() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AudioManager audio = context.getSystemService(AudioManager.class);
        assumeFalse("The playback check needs a quiet device", audio.isMusicActive());

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            assertAudio(audio, true);
            for (Route route : Route.values()) {
                String lord = context.getString(route.nameResource);
                click(By.text(lord));
                assertTrue(device.wait(Until.hasObject(
                        By.text(context.getString(R.string.route_title, lord))), TIMEOUT));
                assertFalse(device.hasObject(By.desc("Música de la senda")));
                assertAudio(audio, true);

                if (route == Route.CAI) {
                    click(By.res(PACKAGE, "manage_units").enabled(true));
                    assertTrue(device.wait(Until.hasObject(By.text("Unidades reclutadas")), TIMEOUT));
                    assertAudio(audio, true);
                    click(By.res(PACKAGE, "back_to_route"));

                    click(By.res(PACKAGE, "manage_supports").enabled(true));
                    assertTrue(device.wait(Until.hasObject(By.text("Parejas de apoyo")), TIMEOUT));
                    assertAudio(audio, true);
                    click(By.res(PACKAGE, "back_to_route"));
                }

                click(By.res(PACKAGE, "back_to_routes"));
                assertTrue(device.wait(Until.hasObject(By.text(lord)), TIMEOUT));
                assertAudio(audio, true);
            }
        }
        assertAudio(audio, false);
    }

    @Test
    public void pausesAndResumesTheSoundtrackWhenTheAppLeavesTheForeground() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AudioManager audio = context.getSystemService(AudioManager.class);
        assumeFalse("The playback check needs a quiet device", audio.isMusicActive());

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            assertAudio(audio, true);
            scenario.moveToState(Lifecycle.State.CREATED);
            assertAudio(audio, false);
            scenario.moveToState(Lifecycle.State.RESUMED);
            assertAudio(audio, true);
        }
        assertAudio(audio, false);
    }

    private static void assertAudio(AudioManager audio, boolean expected) {
        long deadline = SystemClock.uptimeMillis() + TIMEOUT;
        while (audio.isMusicActive() != expected && SystemClock.uptimeMillis() < deadline) {
            SystemClock.sleep(50);
        }
        assertEquals("Audio state", expected, audio.isMusicActive());
    }

    private void click(BySelector selector) {
        UiObject2 view = device.wait(Until.findObject(selector), TIMEOUT);
        assertNotNull("Missing " + selector, view);
        view.click();
    }
}
