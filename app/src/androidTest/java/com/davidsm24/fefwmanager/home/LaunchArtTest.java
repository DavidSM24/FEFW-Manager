package com.davidsm24.fefwmanager.home;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleCallback;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.davidsm24.fefwmanager.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
public final class LaunchArtTest {
    @Test
    public void showsTheIllustrationBeforeTheMenuAndSkipsItOnRecreation() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        AtomicBoolean illustratedAtLaunch = new AtomicBoolean();
        ActivityLifecycleCallback callback = (activity, stage) -> {
            if (activity instanceof MainActivity && stage == Stage.STARTED) {
                ImageView illustration = activity.findViewById(R.id.launch_illustration);
                illustratedAtLaunch.set(illustration != null && illustration.getDrawable() != null);
            }
        };
        instrumentation.runOnMainSync(() ->
                ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback(callback));
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            assertTrue("Missing launch artwork", illustratedAtLaunch.get());
            UiDevice device = UiDevice.getInstance(instrumentation);
            String app = instrumentation.getTargetContext().getPackageName();
            assertTrue("The launch artwork did not give way to the menu",
                    device.wait(Until.hasObject(By.res(app, "app_title")), 8_000));
            scenario.onActivity(activity -> assertNull(activity.findViewById(R.id.launch_art)));

            scenario.recreate();
            scenario.onActivity(activity -> assertNull(activity.findViewById(R.id.launch_art)));
        } finally {
            instrumentation.runOnMainSync(() ->
                    ActivityLifecycleMonitorRegistry.getInstance().removeLifecycleCallback(callback));
        }
    }
}
