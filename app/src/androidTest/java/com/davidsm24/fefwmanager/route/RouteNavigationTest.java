package com.davidsm24.fefwmanager.route;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

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
import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.MainMission;
import com.davidsm24.fefwmanager.data.ProgressDatabase;
import com.davidsm24.fefwmanager.data.RecruitedUnit;
import com.davidsm24.fefwmanager.data.RouteProgressDao;
import com.davidsm24.fefwmanager.home.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.EnumMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class RouteNavigationTest {
    private static final String PACKAGE = "com.davidsm24.fefwmanager";
    private static final long TIMEOUT = 8000;
    private final UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    @Test
    public void opensEachRouteAndReturnsToMenu() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            assertVisible(By.res(PACKAGE, "app_title"));
            for (String name : new String[]{"Cai", "Theodora", "Dietrich", "Leda"}) {
                click(By.text(name));
                assertVisible(By.text("Senda de " + name));
                assertVisible(By.res(PACKAGE, "manage_mission").enabled(true));
                assertVisible(By.res(PACKAGE, "manage_units").enabled(true));
                assertVisible(By.res(PACKAGE, "manage_supports").enabled(true));
                click(By.res(PACKAGE, "back_to_routes"));
                assertVisible(By.res(PACKAGE, "app_title"));
            }
        }
    }

    @Test
    public void canReadAllThreeSectionsFromRoute() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            click(By.text("Cai"));
            click(By.res(PACKAGE, "manage_mission").enabled(true));
            assertVisible(By.res(PACKAGE, "mission_day"));
            assertVisible(By.res(PACKAGE, "mission_month"));
            assertVisible(By.res(PACKAGE, "mission_description"));
            click(By.res("android", "button2"));

            click(By.res(PACKAGE, "manage_units").enabled(true));
            assertVisible(By.res(PACKAGE, "add_unit").enabled(true));
            click(By.res(PACKAGE, "back_to_route"));

            click(By.res(PACKAGE, "manage_supports").enabled(true));
            assertVisible(By.text("Parejas de apoyo"));
            click(By.res(PACKAGE, "back_to_route"));
            assertVisible(By.text("Senda de Cai"));
        }
    }

    @Test
    public void managesTheSameUnitIndependentlyInTwoRoutes() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RouteProgressDao progress = ((FefwApplication) context.getApplicationContext()).routeProgress();
        ProgressDatabase fixture = ProgressDatabase.open(context, "route-progress.db");
        RecruitedUnit cai = new RecruitedUnit(Route.CAI, "alexandra");
        RecruitedUnit leda = new RecruitedUnit(Route.LEDA, "alexandra");

        try {
            RecruitedUnit caiBefore = fixture.routeProgress().findRecruit(cai.route, cai.unitId);
            RecruitedUnit ledaBefore = fixture.routeProgress().findRecruit(leda.route, leda.unitId);
            try {
                progress.removeRecruit(cai);
                progress.removeRecruit(leda);
                try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
                    openUnits("Cai");
                    openAlexandraClassPicker();
                    click(By.res("android", "button2"));
                    assertTrue(device.wait(Until.gone(By.text("Elegir clase para Alexandra")), TIMEOUT));
                    assertNull(fixture.routeProgress().findRecruit(cai.route, cai.unitId));
                    openAlexandraClassPicker();
                    chooseClass("Archer", " aRcH ");
                    assertEquals("archer", fixture.routeProgress().findRecruit(cai.route, cai.unitId).classId);
                    UiObject2 archerIcon = find(By.res(PACKAGE, "recruited_class_icon").desc("Archer"));
                    assertTrue(archerIcon.getVisibleBounds().right
                            <= find(By.desc("Quitar a Alexandra")).getVisibleBounds().left);
                    assertClassTooltip(archerIcon, "Archer");
                    click(By.res(PACKAGE, "back_to_route"));
                    click(By.res(PACKAGE, "back_to_routes"));

                    openUnits("Leda");
                    openAlexandraClassPicker();
                    chooseClass("Bishop", "bIsh");
                    assertEquals("bishop", fixture.routeProgress().findRecruit(leda.route, leda.unitId).classId);
                    assertVisible(By.res(PACKAGE, "recruited_class_icon").desc("Bishop"));
                    click(By.res(PACKAGE, "back_to_route"));
                    click(By.res(PACKAGE, "back_to_routes"));

                    BySelector remove = By.desc("Quitar a Alexandra");
                    openUnits("Cai");
                    click(remove);
                    click(By.res("android", "button2"));
                    assertVisible(remove);
                    click(remove);
                    click(By.res("android", "button1"));
                    assertTrue("Alexandra should be removed from Cai",
                            device.wait(Until.gone(remove), TIMEOUT));
                    click(By.res(PACKAGE, "back_to_route"));
                    click(By.res(PACKAGE, "back_to_routes"));

                    openUnits("Leda");
                    assertVisible(remove);
                    click(By.res(PACKAGE, "back_to_route"));
                    click(By.res(PACKAGE, "back_to_routes"));
                }
            } finally {
                restoreRecruit(progress, cai, caiBefore);
                restoreRecruit(progress, leda, ledaBefore);
            }
        } finally {
            fixture.close();
        }
    }

    private void openUnits(String route) {
        click(By.text(route));
        click(By.res(PACKAGE, "manage_units").enabled(true));
        assertVisible(By.res(PACKAGE, "add_unit").enabled(true));
    }

    private void openAlexandraClassPicker() {
        click(By.res(PACKAGE, "add_unit").enabled(true));
        BySelector search = By.res(PACKAGE, "unit_search");
        find(search).setText("zzzz");
        assertVisible(By.res(PACKAGE, "unit_search_empty"));
        find(search).setText("");
        assertVisible(By.res(PACKAGE, "unit_name").text("Alexandra"));
        find(search).setText(" eXan ");
        assertTrue(device.wait(Until.gone(By.res(PACKAGE, "unit_search_empty")), TIMEOUT));
        click(By.res(PACKAGE, "unit_name").text("Alexandra"));
        assertVisible(By.text("Elegir clase para Alexandra"));
    }

    private void chooseClass(String name, String filter) {
        BySelector search = By.res(PACKAGE, "unit_search");
        find(search).setText("zzzz");
        assertVisible(By.text("No hay clases que coincidan."));
        find(search).setText(filter);
        assertVisible(By.res(PACKAGE, "class_icon"));
        click(By.res(PACKAGE, "class_name").text(name));
        assertVisible(By.desc("Quitar a Alexandra"));
    }

    private void assertClassTooltip(UiObject2 icon, String name) {
        icon.longClick();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            var automation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
            AccessibilityNodeInfo root = automation.getRootInActiveWindow();
            assertNotNull(root);
            AccessibilityNodeInfo node = root.findAccessibilityNodeInfosByViewId(
                    PACKAGE + ":id/recruited_class_icon").stream()
                    .filter(candidate -> name.equals(candidate.getContentDescription()))
                    .findFirst().orElse(null);
            assertNotNull(node);
            assertTrue(node.refresh());
            assertEquals(name, String.valueOf(node.getTooltipText()));
            assertTrue(node.getActionList().contains(
                    AccessibilityNodeInfo.AccessibilityAction.ACTION_HIDE_TOOLTIP));
        }
    }

    private static void restoreRecruit(RouteProgressDao progress, RecruitedUnit unit,
            RecruitedUnit previous) {
        progress.removeRecruit(unit);
        if (previous != null) {
            progress.recruit(previous);
        }
    }

    @Test
    public void editsOnlyTheSelectedMissionAndReordersRoutes() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RouteProgressDao progress = ((FefwApplication) context.getApplicationContext()).routeProgress();
        ProgressDatabase fixture = ProgressDatabase.open(context, "route-progress.db");
        MainMission caiBefore = readMission(fixture, Route.CAI);
        MainMission theodoraBefore = readMission(fixture, Route.THEODORA);
        String updatedDescription = "Nueva misión\nReúne a las unidades";

        try {
            progress.saveMission(new MainMission(Route.CAI, 7, 5, "Descripción inicial"));
            progress.saveMission(new MainMission(Route.THEODORA, 4, 5, "Misión de Theodora"));

            try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
                assertVisible(By.text("4/5"));
                assertAbove("Theodora", "Cai");
                click(By.text("Cai"));
                assertMissionDescription("Descripción inicial");
                click(By.res(PACKAGE, "manage_mission").enabled(true));
                assertVisible(By.res(PACKAGE, "mission_description").text("Descripción inicial"));

                editMission("31", "4", "Cambios descartados");
                click(By.res("android", "button1"));
                assertVisible(By.res(PACKAGE, "mission_description").text("Cambios descartados"));
                click(By.res("android", "button2"));
                assertVisible(By.textContains("7/5"));
                assertMissionDescription("Descripción inicial");
                click(By.res(PACKAGE, "manage_mission").enabled(true));
                assertVisible(By.res(PACKAGE, "mission_description").text("Descripción inicial"));

                editMission("5", "4", updatedDescription);
                click(By.res("android", "button1"));
                assertVisible(By.textContains("5/4"));
                assertMissionDescription(updatedDescription);
                click(By.res(PACKAGE, "back_to_routes"));
                assertVisible(By.text("5/4"));
                assertAbove("Cai", "Theodora");

                click(By.text("Cai"));
                assertMissionDescription(updatedDescription);
                click(By.res(PACKAGE, "manage_mission").enabled(true));
                assertVisible(By.res(PACKAGE, "mission_day").text("5"));
                assertVisible(By.res(PACKAGE, "mission_month").text("4"));
                assertVisible(By.res(PACKAGE, "mission_description").text(updatedDescription));
                click(By.res("android", "button2"));

                click(By.res(PACKAGE, "back_to_routes"));
                click(By.text("Theodora"));
                assertMissionDescription("Misión de Theodora");
                click(By.res(PACKAGE, "manage_mission").enabled(true));
                assertVisible(By.res(PACKAGE, "mission_description").text("Misión de Theodora"));
                editMission("4", "5", "");
                click(By.res("android", "button1"));
                assertTrue(device.wait(Until.gone(By.res(PACKAGE, "mission_description")), TIMEOUT));
                assertVisible(By.res(PACKAGE, "mission_summary").textContains("4/5"));
                assertTrue(device.wait(
                        Until.gone(By.res(PACKAGE, "mission_summary_description")), TIMEOUT));
                click(By.res(PACKAGE, "back_to_routes"));
            }
        } finally {
            try {
                restoreMission(fixture, progress, Route.CAI, caiBefore);
                restoreMission(fixture, progress, Route.THEODORA, theodoraBefore);
            } finally {
                fixture.close();
            }
        }
    }

    private static MainMission readMission(ProgressDatabase database, Route route) {
        try (Cursor cursor = database.getOpenHelper().getReadableDatabase().query(
                new SimpleSQLiteQuery(
                        "SELECT day, month, description FROM main_missions WHERE route_id = ?",
                        new Object[]{route.id}))) {
            return cursor.moveToFirst()
                    ? new MainMission(route, cursor.getInt(0), cursor.getInt(1), cursor.getString(2))
                    : null;
        }
    }

    @Test
    public void summarizesPlayOrderOnlyWhenAtLeastTwoRoutesHaveDates() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RouteProgressDao progress = ((FefwApplication) context.getApplicationContext()).routeProgress();
        ProgressDatabase fixture = ProgressDatabase.open(context, "route-progress.db");
        BySelector summary = By.res(PACKAGE, "play_order_summary");

        try {
            Map<Route, MainMission> before = new EnumMap<>(Route.class);
            for (Route route : Route.values()) {
                before.put(route, readMission(fixture, route));
            }
            try {
                for (Route route : Route.values()) {
                    restoreMission(fixture, progress, route, null);
                }
                try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
                    assertVisible(By.res(PACKAGE, "route_deadline").text("Sin fecha"));
                    assertFalse(device.hasObject(summary));

                    progress.saveMission(new MainMission(Route.LEDA, 5, 6, ""));
                    assertVisible(By.res(PACKAGE, "route_deadline").text("5/6"));
                    assertAbove("Leda", "Cai");
                    assertFalse(device.hasObject(summary));

                    progress.saveMission(new MainMission(Route.CAI, 10, 5, ""));
                    assertVisible(By.res(PACKAGE, "play_order_summary").desc("Cai → Leda"));
                    assertAbove("Cai", "Leda");
                    assertAbove("Leda", "Theodora");

                    progress.saveMission(new MainMission(Route.DIETRICH, 13, 6, ""));
                    progress.saveMission(new MainMission(Route.THEODORA, 28, 5, ""));
                    assertVisible(By.res(PACKAGE, "play_order_summary")
                            .desc("Cai → Theodora → Leda → Dietrich"));
                    scenario.onActivity(activity -> {
                        TextView sequence = activity.findViewById(R.id.play_order_summary);
                        Spanned text = (Spanned) sequence.getText();
                        assertEquals(4, text.getSpans(0, text.length(), ImageSpan.class).length);
                    });

                    progress.saveMission(new MainMission(Route.LEDA, 5, 5, ""));
                    assertVisible(By.res(PACKAGE, "play_order_summary")
                            .desc("Leda → Cai → Theodora → Dietrich"));
                    assertAbove("Leda", "Cai");
                }
            } finally {
                for (Route route : Route.values()) {
                    restoreMission(fixture, progress, route, before.get(route));
                }
            }
        } finally {
            fixture.close();
        }
    }

    private static void restoreMission(ProgressDatabase database, RouteProgressDao progress,
            Route route, MainMission mission) {
        if (mission == null) {
            database.getOpenHelper().getWritableDatabase()
                    .delete("main_missions", "route_id = ?", new Object[]{route.id});
        } else {
            progress.saveMission(mission);
        }
    }

    private void editMission(String day, String month, String description) {
        find(By.res(PACKAGE, "mission_day")).setText(day);
        find(By.res(PACKAGE, "mission_month")).setText(month);
        find(By.res(PACKAGE, "mission_description")).setText(description);
    }

    private void assertMissionDescription(String description) {
        assertVisible(By.res(PACKAGE, "mission_summary_description").text(description));
    }

    private void assertAbove(String first, String second) {
        assertTrue(first + " should appear before " + second,
                find(By.text(first)).getVisibleBounds().top
                        < find(By.text(second)).getVisibleBounds().top);
    }

    private void assertVisible(BySelector selector) {
        assertTrue("Missing UI element " + selector, device.wait(Until.hasObject(selector), TIMEOUT));
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
