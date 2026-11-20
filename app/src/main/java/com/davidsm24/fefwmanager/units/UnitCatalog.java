package com.davidsm24.fefwmanager.units;

import androidx.annotation.Nullable;

import com.davidsm24.fefwmanager.route.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class UnitCatalog {
    public record LoadResult(@Nullable UnitCatalog catalog) {
    }

    private final Map<String, Entry> entries;

    private UnitCatalog(Map<String, Entry> entries) {
        this.entries = Collections.unmodifiableMap(entries);
    }

    public static UnitCatalog read(InputStream resource) throws IOException, JSONException {
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line).append('\n');
            }
        }

        JSONArray units = new JSONObject(json.toString()).getJSONArray("unidades");
        Map<String, Entry> entries = new LinkedHashMap<>();
        for (int i = 0; i < units.length(); i++) {
            JSONObject unit = units.getJSONObject(i);
            String id = unit.getString("id");
            String name = unit.getString("nombre");
            JSONArray routeIds = unit.getJSONArray("sendasParte1");
            EnumSet<Route> routes = EnumSet.noneOf(Route.class);
            for (int j = 0; j < routeIds.length(); j++) {
                Route route = Route.fromId(routeIds.getString(j));
                if (route == null) {
                    throw new JSONException("Unexpected route in the unit catalog");
                }
                routes.add(route);
            }
            if (id.isEmpty() || name.isEmpty() || entries.containsKey(id)) {
                throw new JSONException("Invalid unit in the catalog");
            }
            entries.put(id, new Entry(new UnitLabel(id, name), routes));
        }
        return new UnitCatalog(entries);
    }

    public UnitLabel labelFor(String id) {
        Entry entry = entries.get(id);
        return entry == null ? new UnitLabel(id, id) : entry.label;
    }

    List<UnitLabel> availableFor(Route route, List<String> recruitedIds) {
        List<UnitLabel> candidates = new ArrayList<>();
        for (Entry entry : entries.values()) {
            if (entry.routes.contains(route) && !recruitedIds.contains(entry.label.id())) {
                candidates.add(entry.label);
            }
        }
        return candidates;
    }

    private static final class Entry {
        private final UnitLabel label;
        private final EnumSet<Route> routes;

        private Entry(UnitLabel label, EnumSet<Route> routes) {
            this.label = label;
            this.routes = routes;
        }
    }
}
