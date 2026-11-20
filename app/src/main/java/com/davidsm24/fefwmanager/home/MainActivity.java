package com.davidsm24.fefwmanager.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.ComponentActivity;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.MainMission;
import com.davidsm24.fefwmanager.databinding.ActivityMainBinding;
import com.davidsm24.fefwmanager.databinding.ItemRouteBinding;
import com.davidsm24.fefwmanager.route.Route;
import com.davidsm24.fefwmanager.route.RouteActivity;
import com.davidsm24.fefwmanager.ui.ImperialMotion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
        if (savedInstanceState == null) {
            ImperialMotion.reveal(binding.heroHeading);
        }

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
                .observe(this, missions -> renderMissions(binding, missions));
    }

    private void renderMissions(ActivityMainBinding binding, List<MainMission> missions) {
        Map<Route, MainMission> byRoute = new EnumMap<>(Route.class);
        for (MainMission mission : missions) {
            byRoute.put(mission.route, mission);
        }
        Route[] ordered = Route.values();
        // The stable sort keeps menu order for ties and puts routes without dates last.
        Arrays.sort(ordered, Comparator.comparingInt(route -> {
            MainMission mission = byRoute.get(route);
            return mission == null ? Integer.MAX_VALUE : mission.month * 100 + mission.day;
        }));

        LinearLayout routeCards = binding.routeCards;
        List<String> playOrder = new ArrayList<>();
        for (int index = 0; index < ordered.length; index++) {
            Route route = ordered[index];
            ItemRouteBinding card = cards.get(route);
            MainMission mission = byRoute.get(route);
            String deadline = mission == null
                    ? getString(R.string.no_deadline)
                    : getString(R.string.deadline_format, mission.day, mission.month);
            card.routeDeadline.setText(deadline);
            if (mission != null) {
                playOrder.add(getString(route.nameResource));
            }
            if (routeCards.getChildAt(index) != card.getRoot()) {
                routeCards.removeView(card.getRoot());
                routeCards.addView(card.getRoot(), index);
            }
        }

        if (playOrder.size() >= 2) {
            binding.playOrderSummary.setText(TextUtils.join(
                    getString(R.string.play_order_separator), playOrder));
            binding.playOrderPanel.setVisibility(View.VISIBLE);
        } else {
            binding.playOrderPanel.setVisibility(View.GONE);
        }
    }
}
