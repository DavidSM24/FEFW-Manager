package com.davidsm24.fefwmanager.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.davidsm24.fefwmanager.route.Route;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
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

        ProgressDatabase database = ProgressDatabase.open(context, name);
        try {
            RouteProgressDao progress = database.routeProgress();
            progress.saveMission(new MainMission(Route.CAI, 10, 5, "Misión inicial"));
            progress.saveMission(new MainMission(Route.CAI, 11, 5, "Misión actualizada"));
            progress.saveMission(new MainMission(Route.THEODORA, 28, 5, "Misión de Theodora"));

            progress.recruit(new RecruitedUnit(Route.CAI, "tialla", "archer"));
            progress.recruit(new RecruitedUnit(Route.CAI, "tialla", "bishop"));
            assertEquals("archer", progress.findRecruit(Route.CAI, "tialla").classId);
            progress.recruit(new RecruitedUnit(Route.DIETRICH, "tialla"));

            progress.addSupport(new SupportPair(Route.CAI, "tialla", "peter"));
            progress.addSupport(new SupportPair(Route.CAI, "peter", "tialla"));
            progress.addSupport(new SupportPair(Route.DIETRICH, "tialla", "peter"));
            progress.changeRecruitClass(Route.CAI, "tialla", "bishop");

            database.close();
            database = ProgressDatabase.open(context, name);
            progress = database.routeProgress();

            RouteProgress cai = observeOnce(progress.observeProgress(Route.CAI));
            assertEquals(Integer.valueOf(11), cai.day);
            assertEquals(Integer.valueOf(5), cai.month);
            assertEquals("Misión actualizada", cai.description);
            assertEquals(1, cai.unitCount);
            assertEquals(1, cai.supportCount);
            assertEquals("bishop", progress.findRecruit(Route.CAI, "tialla").classId);
            assertNull(progress.findRecruit(Route.DIETRICH, "tialla").classId);

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
            assertEquals(Collections.singletonList("tialla"),
                    observeOnce(progress.observeRecruitedUnitIds(Route.CAI)));
            progress.removeRecruit(new RecruitedUnit(Route.CAI, "tialla"));
            assertTrue(observeOnce(progress.observeRecruitedUnitIds(Route.CAI)).isEmpty());
            assertEquals(Collections.singletonList("tialla"),
                    observeOnce(progress.observeRecruitedUnitIds(Route.DIETRICH)));
            assertEquals(1, observeOnce(progress.observeProgress(Route.CAI)).supportCount);

            SupportPair newPair = new SupportPair(Route.THEODORA, "tialla", "peter");
            assertEquals(RouteProgressDao.SupportCreation.MISSING_RECRUITS,
                    progress.createSupport(newPair));
            progress.recruit(new RecruitedUnit(Route.THEODORA, "tialla"));
            progress.recruit(new RecruitedUnit(Route.THEODORA, "peter"));
            assertEquals(RouteProgressDao.SupportCreation.ADDED, progress.createSupport(newPair));
            assertEquals(RouteProgressDao.SupportCreation.DUPLICATE,
                    progress.createSupport(new SupportPair(Route.THEODORA, "peter", "tialla")));
            assertEquals(1, observeOnce(progress.observeSupportPairs(Route.THEODORA)).size());
            progress.removeSupport(newPair);
            assertTrue(observeOnce(progress.observeSupportPairs(Route.THEODORA)).isEmpty());
            assertEquals(1, observeOnce(progress.observeSupportPairs(Route.CAI)).size());
        } finally {
            database.close();
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
