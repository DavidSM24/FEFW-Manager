package com.davidsm24.fefwmanager.route;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.davidsm24.fefwmanager.home.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

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
            assertVisible(By.textContains("Fecha límite"));
            click(By.text("Cerrar"));

            click(By.res(PACKAGE, "manage_units").enabled(true));
            assertVisible(By.textContains("reclutada"));
            click(By.text("Cerrar"));

            click(By.res(PACKAGE, "manage_supports").enabled(true));
            assertVisible(By.textContains("de apoyo"));
            click(By.text("Cerrar"));
            assertVisible(By.text("Senda de Cai"));
        }
    }

    private void assertVisible(BySelector selector) {
        assertTrue("Missing UI element " + selector, device.wait(Until.hasObject(selector), TIMEOUT));
    }

    private void click(BySelector selector) {
        UiObject2 object = device.wait(Until.findObject(selector), TIMEOUT);
        assertNotNull("Missing UI element " + selector, object);
        object.click();
    }
}
