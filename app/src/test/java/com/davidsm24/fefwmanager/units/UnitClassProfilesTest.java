package com.davidsm24.fefwmanager.units;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public final class UnitClassProfilesTest {
    @Test
    public void bundledClassCatalogMatchesValidRoleProfiles() throws IOException {
        InputStream catalog = UnitClassProfilesTest.class.getResourceAsStream("/catalog.json");
        assertNotNull("Missing bundled class catalog on the test classpath", catalog);
        try (InputStreamReader reader = new InputStreamReader(catalog, StandardCharsets.UTF_8)) {
            JsonArray classes = JsonParser.parseReader(reader).getAsJsonArray();
            assertFalse("The class catalog must contain entries", classes.isEmpty());
            Set<String> ids = new HashSet<>();
            for (JsonElement entry : classes) {
                String id = entry.getAsJsonObject().get("id").getAsString();
                assertTrue("Duplicate class in the catalog: " + id, ids.add(id));

                ClassRoleProfile profile = ClassRoleCatalog.forClass(id);
                assertNotNull("Missing role profile for " + id, profile);
                assertTrue("Role scores must be between 0 and 5 for " + id,
                        IntStream.of(profile.atk(), profile.def(), profile.mov(),
                                profile.sup(), profile.heal()).allMatch(score -> score >= 0 && score <= 5));
                assertFalse("Null tactical tag for " + id, profile.tags().contains(null));
            }
            assertEquals("The role profiles must match the bundled classes", ids, ClassRoleCatalog.classIds());
        }
    }
}
