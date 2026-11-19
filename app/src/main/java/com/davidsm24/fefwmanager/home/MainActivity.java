package com.davidsm24.fefwmanager.home;

import android.os.Bundle;

import androidx.activity.ComponentActivity;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.MainMission;
import com.davidsm24.fefwmanager.databinding.ActivityMainBinding;
import com.davidsm24.fefwmanager.databinding.ItemRouteBinding;
import com.davidsm24.fefwmanager.route.Route;
import com.davidsm24.fefwmanager.route.RouteActivity;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class MainActivity extends ComponentActivity {
    private final Map<Route, ItemRouteBinding> cards = new EnumMap<>(Route.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        for (Route route : Route.values()) {
            ItemRouteBinding card = ItemRouteBinding.inflate(getLayoutInflater(),
                    binding.routeCards, false);
            String name = getString(route.nameResource);
            card.routeName.setText(name);
            card.routeInitial.setText(name.substring(0, 1));
            card.routeInitial.setTextColor(getColor(route.accentResource));
            card.getRoot().setOnClickListener(view -> startActivity(RouteActivity.intent(this, route)));
            binding.routeCards.addView(card.getRoot());
            cards.put(route, card);
        }

        ((FefwApplication) getApplication()).routeProgress().observeMissions()
                .observe(this, this::renderMissions);
    }

    private void renderMissions(List<MainMission> missions) {
        Map<Route, MainMission> byRoute = new EnumMap<>(Route.class);
        for (MainMission mission : missions) {
            byRoute.put(mission.route, mission);
        }
        cards.forEach((route, card) -> {
            MainMission mission = byRoute.get(route);
            String deadline = mission == null
                    ? getString(R.string.no_deadline)
                    : getString(R.string.deadline_format, mission.day, mission.month);
            card.routeDeadline.setText(deadline);
        });
    }
}
