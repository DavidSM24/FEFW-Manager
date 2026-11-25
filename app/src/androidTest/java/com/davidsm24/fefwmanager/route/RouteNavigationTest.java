package com.davidsm24.fefwmanager.route;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;
import android.text.Spanned;
import android.text.style.ImageSpan;
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
    public void groupsSelectableClassesByTier() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RouteProgressDao progress = ((FefwApplication) context.getApplicationContext()).routeProgress();
        RecruitedUnit recruit = new RecruitedUnit(Route.CAI, "alexandra");
        RecruitedUnit original = progress.findRecruit(recruit.route, recruit.unitId);
        progress.removeRecruit(recruit);
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            openUnits("Cai");
            openAlexandraClassPicker();
            BySelector initial = By.res(PACKAGE, "class_tier_heading").text("Inicial");
            BySelector commoner = By.res(PACKAGE, "class_name").text("Commoner");
            assertTrue(find(initial).getVisibleBounds().bottom <= find(commoner).getVisibleBounds().top);
            assertTrue(find(commoner).getVisibleBounds().bottom
                    <= find(By.res(PACKAGE, "class_name").text("Diviner")).getVisibleBounds().top);
            click(initial);
            assertVisible(By.text("Elegir clase para Alexandra"));
            assertNull(progress.findRecruit(recruit.route, recruit.unitId));

            assertClassFilter("Commoner", "Inicial");
            assertClassFilter("Archer", "Intermedia");
            assertClassFilter("Bishop", "Avanzada");
            assertClassFilter("Swordmaster", "Suprema");
            assertClassFilter("The Avatar", "Divina");
            click(By.res(PACKAGE, "class_name").text("The Avatar"));
            assertVisible(By.res(PACKAGE, "recruited_class_icon").descContains("The Avatar"));
            assertEquals("the-avatar", progress.findRecruit(recruit.route, recruit.unitId).classId);
        } finally {
            restoreRecruit(progress, recruit, original);
        }
    }

    @Test
    public void inspectsUnitsAndOpensTheirClassDetails() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RouteProgressDao progress = ((FefwApplication) context.getApplicationContext()).routeProgress();
        RecruitedUnit recruit = new RecruitedUnit(Route.CAI, "alexandra");
        RecruitedUnit original = progress.findRecruit(recruit.route, recruit.unitId);
        progress.removeRecruit(recruit);
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            BySelector caiPortrait = By.res(PACKAGE, "inspect_unit_portrait").desc("Ver ficha de Cai");
            BySelector caiTitle = By.res(PACKAGE, "detail_unit_name").text("Cai");
            BySelector alexandraTitle = By.res(PACKAGE, "detail_unit_name").text("Alexandra");
            hold(caiPortrait);
            assertVisible(caiTitle);
            assertVisible(By.res(PACKAGE, "detail_unit_description").textContains("Ribeira"));
            assertVisible(By.res(PACKAGE, "detail_unit_class_name").text("Commoner"));
            dismissFromOutside(caiTitle);
            click(caiPortrait);
            assertVisible(By.text("Senda de Cai"));
            click(By.res(PACKAGE, "manage_units").enabled(true));
            click(By.res(PACKAGE, "add_unit").enabled(true));

            BySelector search = By.res(PACKAGE, "unit_search");
            find(search).setText(" eXan ");
            BySelector portrait = By.res(PACKAGE, "inspect_unit_portrait").desc("Ver ficha de Alexandra");
            hold(portrait);
            assertVisible(alexandraTitle);
            assertVisible(By.res(PACKAGE, "detail_unit_description").textContains("pegaso"));
            assertVisible(By.res(PACKAGE, "detail_unit_class_name").text("Wing Soldier"));
            assertVisible(By.text("Clase de referencia"));
            dismissFromOutside(alexandraTitle);
            assertVisible(search.text(" eXan "));
            click(portrait);
            assertVisible(By.text("Elegir clase para Alexandra"));
            chooseClass("Archer", "arch");
            assertVisible(By.res(PACKAGE, "recruited_class_icon").descContains("Archer"));

            hold(portrait);
            assertVisible(By.res(PACKAGE, "detail_unit_class_name").text("Archer"));
            assertVisible(By.text("Clase en esta senda"));
            click(By.res(PACKAGE, "detail_unit_class_icon"));
            BySelector classTitle = By.res(PACKAGE, "detail_class_name").text("Archer");
            assertVisible(classTitle);
            assertVisible(By.res(PACKAGE, "detail_class_description").textContains("a distancia"));
            dismissFromOutside(classTitle);
            assertVisible(alexandraTitle);
            dismissFromOutside(alexandraTitle);
            assertVisible(portrait);
            assertEquals("archer", progress.findRecruit(recruit.route, recruit.unitId).classId);
        } finally {
            restoreRecruit(progress, recruit, original);
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
                    openGroupAnalysis();
                    assertVisible(By.res(PACKAGE, "group_attack").desc("Ataque, 1 de 5"));
                    assertVisible(By.text("Asigna una clase a una unidad para evaluar el grupo."));
                    click(By.res(PACKAGE, "group_attack"));
                    dismissGroupAnalysisFromOutside();
                    openAlexandraClassPicker();
                    click(By.res("android", "button2"));
                    assertTrue(device.wait(Until.gone(By.text("Elegir clase para Alexandra")), TIMEOUT));
                    assertNull(fixture.routeProgress().findRecruit(cai.route, cai.unitId));
                    openAlexandraClassPicker();
                    filterClasses(" aRcH ");
                    BySelector pickerIcon = By.res(PACKAGE, "class_icon").descContains("Archer");
                    inspectArcher(pickerIcon);
                    assertVisible(By.text("Elegir clase para Alexandra"));
                    assertVisible(By.res(PACKAGE, "unit_search").text(" aRcH "));
                    assertNull(fixture.routeProgress().findRecruit(cai.route, cai.unitId));
                    click(pickerIcon);
                    BySelector archer = By.res(PACKAGE, "recruited_class_icon").descContains("Archer");
                    assertVisible(archer);
                    openGroupAnalysis();
                    assertVisible(By.res(PACKAGE, "group_attack").desc("Ataque, 4 de 5"));
                    assertEquals("archer", fixture.routeProgress().findRecruit(cai.route, cai.unitId).classId);
                    assertVisible(By.text("Alertas tácticas"));
                    assertVisible(By.res(PACKAGE, "group_alerts").textContains("Falta daño mágico."));
                    dismissGroupAnalysisFromOutside();
                    UiObject2 archerIcon = find(archer);
                    assertTrue(archerIcon.getVisibleBounds().right
                            <= find(By.desc("Quitar a Alexandra")).getVisibleBounds().left);
                    inspectArcher(archer);
                    assertVisible(archer);
                    click(archer);
                    assertVisible(By.text("Cambiar clase de Alexandra"));
                    chooseClass("Diviner", "divin");
                    assertVisible(By.res(PACKAGE, "recruited_class_icon").descContains("Diviner"));
                    assertEquals("diviner", fixture.routeProgress().findRecruit(cai.route, cai.unitId).classId);
                    openGroupAnalysis();
                    assertVisible(By.res(PACKAGE, "group_healing").desc("Curación, 4 de 5"));
                    assertVisible(By.res(PACKAGE, "group_alerts").textContains("Falta daño físico."));
                    assertFalse(device.hasObject(By.res(PACKAGE, "group_alerts")
                            .textContains("Falta daño mágico.")));
                    dismissGroupAnalysisFromOutside();
                    click(By.res(PACKAGE, "back_to_route"));
                    click(By.res(PACKAGE, "back_to_routes"));

                    openUnits("Leda");
                    openAlexandraClassPicker();
                    chooseClass("Bishop", "bIsh");
                    assertVisible(By.res(PACKAGE, "recruited_class_icon").descContains("Bishop"));
                    assertEquals("bishop", fixture.routeProgress().findRecruit(leda.route, leda.unitId).classId);
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
                    openGroupAnalysis();
                    assertVisible(By.res(PACKAGE, "group_evaluated_units").textEndsWith("0/0"));
                    assertVisible(By.text("Asigna una clase a una unidad para evaluar el grupo."));
                    dismissGroupAnalysisFromOutside();
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
        assertVisible(By.res(PACKAGE, "show_group_analysis").enabled(true));
        assertFalse(device.hasObject(By.res(PACKAGE, "group_evaluated_units")));
    }

    private void openGroupAnalysis() {
        click(By.res(PACKAGE, "show_group_analysis").enabled(true));
        assertVisible(By.text("Análisis del grupo"));
    }

    private void dismissGroupAnalysisFromOutside() {
        BySelector title = By.text("Análisis del grupo");
        int tapY = find(title).getVisibleBounds().centerY();
        assertTrue(device.click(2, tapY));
        assertTrue(device.wait(Until.gone(title), TIMEOUT));
        assertVisible(By.res(PACKAGE, "show_group_analysis").enabled(true));
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
        filterClasses(filter);
        click(By.res(PACKAGE, "class_name").text(name));
        assertTrue(device.wait(Until.gone(By.res(PACKAGE, "unit_search")), TIMEOUT));
    }

    private void filterClasses(String filter) {
        BySelector search = By.res(PACKAGE, "unit_search");
        find(search).setText("zzzz");
        assertVisible(By.text("No hay clases que coincidan."));
        find(search).setText(filter);
        assertVisible(By.res(PACKAGE, "class_icon"));
    }

    private void assertClassFilter(String className, String heading) {
        find(By.res(PACKAGE, "unit_search")).setText(className);
        assertVisible(By.res(PACKAGE, "class_name").text(className));
        assertVisible(By.res(PACKAGE, "class_tier_heading").text(heading));
        assertEquals(1, device.findObjects(By.res(PACKAGE, "class_tier_heading")).size());
    }

    private void inspectArcher(BySelector icon) {
        hold(icon);
        BySelector title = By.res(PACKAGE, "detail_class_name").text("Archer");
        assertVisible(By.res(PACKAGE, "detail_class_description")
                .textContains("Combatiente físico a distancia."));
        dismissFromOutside(title);
    }

    private void hold(BySelector icon) {
        var center = find(icon).getVisibleCenter();
        assertTrue(device.swipe(center.x, center.y, center.x, center.y, 260));
    }

    private void dismissFromOutside(BySelector title) {
        assertTrue(device.click(2, find(title).getVisibleBounds().centerY()));
        assertTrue(device.wait(Until.gone(title), TIMEOUT));
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
