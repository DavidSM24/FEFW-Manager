package com.davidsm24.fefwmanager.supports;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;

import androidx.room.Room;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.data.ProgressDatabase;
import com.davidsm24.fefwmanager.data.RecruitedUnit;
import com.davidsm24.fefwmanager.data.RouteProgressDao;
import com.davidsm24.fefwmanager.data.SupportPair;
import com.davidsm24.fefwmanager.home.MainActivity;
import com.davidsm24.fefwmanager.route.Route;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SupportPairsFlowTest {
    private static final String PACKAGE = "com.davidsm24.fefwmanager";
    private static final long TIMEOUT = 8000;
    private final UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    @Test
    public void createsAndRemovesPairWithoutLosingItWhenARecruitLeaves() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RouteProgressDao progress = ((FefwApplication) context.getApplicationContext()).routeProgress();
        ProgressDatabase fixture = Room.databaseBuilder(context, ProgressDatabase.class,
                "route-progress.db").build();
        RecruitedUnit alexandra = new RecruitedUnit(Route.CAI, "alexandra");
        RecruitedUnit benditz = new RecruitedUnit(Route.CAI, "benditz");
        SupportPair pair = new SupportPair(Route.CAI, alexandra.unitId, benditz.unitId);

        try {
            boolean alexandraWasRecruited = hasRecruit(fixture, alexandra);
            boolean benditzWasRecruited = hasRecruit(fixture, benditz);
            boolean pairWasSaved = hasRow(fixture, """
                    SELECT 1 FROM support_pairs
                    WHERE route_id = ? AND first_unit_id = ? AND second_unit_id = ?
                    """, pair.route.id, pair.firstUnitId, pair.secondUnitId);
            try {
                progress.recruit(alexandra);
                progress.recruit(benditz);
                progress.removeSupport(pair);
                checkSupportFlow();
            } finally {
                if (pairWasSaved) {
                    progress.addSupport(pair);
                } else {
                    progress.removeSupport(pair);
                }
                if (alexandraWasRecruited) {
                    progress.recruit(alexandra);
                } else {
                    progress.removeRecruit(alexandra);
                }
                if (!benditzWasRecruited) {
                    progress.removeRecruit(benditz);
                }
            }
        } finally {
            fixture.close();
        }
    }

    private void checkSupportFlow() {
        BySelector removePair = By.desc("Quitar la pareja de Alexandra y Benditz");
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            click(By.text("Cai"));
            click(By.res(PACKAGE, "manage_supports").enabled(true));
            choosePair("Benditz", "Alexandra");
            find(By.res(PACKAGE, "support_member_name").text("Alexandra"));
            find(By.res(PACKAGE, "support_member_name").text("Benditz"));
            find(removePair);

            choosePair("Alexandra", "Benditz");
            find(By.text("Esta pareja ya está guardada en la senda."));
            click(By.res(PACKAGE, "back_to_route"));

            click(By.res(PACKAGE, "manage_units").enabled(true));
            BySelector removeRecruit = By.desc("Quitar a Alexandra");
            click(removeRecruit);
            click(By.res("android", "button1"));
            assertTrue(device.wait(Until.gone(removeRecruit), TIMEOUT));
            click(By.res(PACKAGE, "back_to_route"));

            click(By.res(PACKAGE, "manage_supports").enabled(true));
            find(By.res(PACKAGE, "first_member")
                    .hasDescendant(By.res(PACKAGE, "support_member_status").text("Fuera del registro")));
            click(removePair);
            click(By.res("android", "button2"));
            find(removePair);
            click(removePair);
            click(By.res("android", "button1"));
            assertTrue(device.wait(Until.gone(removePair), TIMEOUT));
            click(By.res(PACKAGE, "back_to_route"));
            click(By.res(PACKAGE, "back_to_routes"));
        }
    }

    private void choosePair(String first, String second) {
        click(By.res(PACKAGE, "add_support").enabled(true));
        click(By.res(PACKAGE, "unit_name").text(first));
        find(By.text("Elegir pareja para " + first));
        assertFalse(device.hasObject(By.res(PACKAGE, "unit_name").text(first)));
        click(By.res(PACKAGE, "unit_name").text(second));
    }

    private static boolean hasRecruit(ProgressDatabase database, RecruitedUnit unit) {
        return hasRow(database, "SELECT 1 FROM recruited_units WHERE route_id = ? AND unit_id = ?",
                unit.route.id, unit.unitId);
    }

    private static boolean hasRow(ProgressDatabase database, String sql, Object... arguments) {
        try (Cursor cursor = database.getOpenHelper().getReadableDatabase()
                .query(new SimpleSQLiteQuery(sql, arguments))) {
            return cursor.moveToFirst();
        }
    }

    private void click(BySelector selector) {
        find(selector).click();
    }

    private UiObject2 find(BySelector selector) {
        UiObject2 object = device.wait(Until.findObject(selector), TIMEOUT);
        assertNotNull("Missing UI element " + selector, object);
        return object;
    }
}
