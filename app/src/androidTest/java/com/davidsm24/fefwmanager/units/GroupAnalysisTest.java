package com.davidsm24.fefwmanager.units;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.davidsm24.fefwmanager.data.ProgressDatabase;
import com.davidsm24.fefwmanager.data.UnitClass;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public final class GroupAnalysisTest {
    @Test
    public void definesExactlyOneProfileForEveryPlayableClass() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String name = "group-classes-" + UUID.randomUUID() + ".db";
        ProgressDatabase database = ProgressDatabase.open(context, name);
        try {
            Set<String> ids = new HashSet<>();
            for (UnitClass unitClass : database.unitClasses().getAll()) {
                ids.add(unitClass.id);
            }
            assertEquals(60, ids.size());
            assertEquals(ids, ClassRoleCatalog.classIds());
            assertEquals(new ClassRoleProfile(3, 4, 2, 2, 4,
                    Arrays.asList(ClassTag.TANQUE, ClassTag.CURADOR)),
                    ClassRoleCatalog.forClass("guardian"));
        } finally {
            database.close();
            context.deleteDatabase(name);
        }
    }

    @Test
    public void evaluatesTheBestTwoContributionsAndIgnoresUnassignedUnits() {
        assertEquals(new GroupAnalysis(3, 4, 4, 4, 4, 3, 3),
                GroupAnalysis.evaluate(Arrays.asList("armored-knight", "priest", null, "wing-soldier")));
    }

    @Test
    public void keepsGroupScoresOnTheOneToFiveScale() {
        assertEquals(new GroupAnalysis(1, 1, 5, 2, 3, 1, 1),
                GroupAnalysis.evaluate(Collections.singletonList("myrmidon")));
    }

    @Test
    public void hasNoScoresWithoutAClassProfile() {
        assertEquals(new GroupAnalysis(0, 0, 0, 0, 0, 0, 0),
                GroupAnalysis.evaluate(Collections.emptyList()));
        assertEquals(new GroupAnalysis(0, 2, 0, 0, 0, 0, 0),
                GroupAnalysis.evaluate(Arrays.asList(null, "unrecognized-class")));
    }
}
