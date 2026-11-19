package com.davidsm24.fefwmanager.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.davidsm24.fefwmanager.route.Route;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class RouteProgressTest {
    private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

    @Test
    public void retainsIndependentProgressAfterReopeningDatabase() throws InterruptedException {
        Context context = instrumentation.getTargetContext();
        String name = "progress-test-" + UUID.randomUUID() + ".db";

        try {
            try (ProgressDatabase database = Room.databaseBuilder(context, ProgressDatabase.class, name).build()) {
                RouteProgressDao progress = database.routeProgress();
                progress.saveMission(new MainMission(Route.CAI, 10, 5, "Misión inicial"));
                progress.saveMission(new MainMission(Route.CAI, 11, 5, "Misión actualizada"));
                progress.saveMission(new MainMission(Route.THEODORA, 28, 5, "Misión de Theodora"));

                progress.recruit(new RecruitedUnit(Route.CAI, "tialla"));
                progress.recruit(new RecruitedUnit(Route.CAI, "tialla"));
                progress.recruit(new RecruitedUnit(Route.DIETRICH, "tialla"));

                progress.addSupport(new SupportPair(Route.CAI, "tialla", "peter"));
                progress.addSupport(new SupportPair(Route.CAI, "peter", "tialla"));
                progress.addSupport(new SupportPair(Route.DIETRICH, "tialla", "peter"));
            }

            try (ProgressDatabase database = Room.databaseBuilder(context, ProgressDatabase.class, name).build()) {
                RouteProgressDao progress = database.routeProgress();

                RouteProgress cai = observeOnce(progress.observeProgress(Route.CAI));
                assertEquals(Integer.valueOf(11), cai.day);
                assertEquals(Integer.valueOf(5), cai.month);
                assertEquals("Misión actualizada", cai.description);
                assertEquals(1, cai.unitCount);
                assertEquals(1, cai.supportCount);

                RouteProgress theodora = observeOnce(progress.observeProgress(Route.THEODORA));
                assertEquals(Integer.valueOf(28), theodora.day);
                assertEquals("Misión de Theodora", theodora.description);
                assertEquals(0, theodora.unitCount);
                assertEquals(0, theodora.supportCount);

                RouteProgress dietrich = observeOnce(progress.observeProgress(Route.DIETRICH));
                assertNull(dietrich.day);
                assertEquals(1, dietrich.unitCount);
                assertEquals(1, dietrich.supportCount);

                RouteProgress leda = observeOnce(progress.observeProgress(Route.LEDA));
                assertNull(leda.day);
                assertNull(leda.description);
                assertEquals(0, leda.unitCount);
                assertEquals(0, leda.supportCount);

                assertEquals(2, observeOnce(progress.observeMissions()).size());
            }
        } finally {
            context.deleteDatabase(name);
        }
    }

    private <T> T observeOnce(LiveData<T> liveData) throws InterruptedException {
        AtomicReference<T> result = new AtomicReference<>();
        CountDownLatch delivered = new CountDownLatch(1);
        Observer<T> observer = value -> {
            result.set(value);
            delivered.countDown();
        };
        instrumentation.runOnMainSync(() -> liveData.observeForever(observer));
        try {
            assertTrue("Room did not return a result", delivered.await(5, TimeUnit.SECONDS));
            return result.get();
        } finally {
            instrumentation.runOnMainSync(() -> liveData.removeObserver(observer));
        }
    }
}
