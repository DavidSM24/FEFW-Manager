package com.davidsm24.fefwmanager.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.MainMission;
import com.davidsm24.fefwmanager.databinding.ActivityMainBinding;
import com.davidsm24.fefwmanager.databinding.ItemRouteBinding;
import com.davidsm24.fefwmanager.route.Route;
import com.davidsm24.fefwmanager.route.RouteActivity;
import com.davidsm24.fefwmanager.ui.ImperialActivity;
import com.davidsm24.fefwmanager.ui.ImperialMotion;
import com.davidsm24.fefwmanager.units.UnitPortraits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class MainActivity extends ImperialActivity {
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
            UnitPortraits.bind(card.routePortrait, route);
            card.getRoot().setOnClickListener(view -> startActivity(RouteActivity.intent(this, route)));
            UnitPortraits.enableDetails(card.routePortrait, route,
                    unit -> details().showUnit(unit, route), view -> card.getRoot().performClick());
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
        List<Route> playOrder = new ArrayList<>();
        for (int index = 0; index < ordered.length; index++) {
            Route route = ordered[index];
            ItemRouteBinding card = cards.get(route);
            MainMission mission = byRoute.get(route);
            String deadline = mission == null
                    ? getString(R.string.no_deadline)
                    : getString(R.string.deadline_format, mission.day, mission.month);
            card.routeDeadline.setText(deadline);
            if (mission != null) {
                playOrder.add(route);
            }
            if (routeCards.getChildAt(index) != card.getRoot()) {
                routeCards.removeView(card.getRoot());
                routeCards.addView(card.getRoot(), index);
            }
        }

        if (playOrder.size() >= 2) {
            renderPlayOrder(binding.playOrderSummary, playOrder);
            binding.playOrderPanel.setVisibility(View.VISIBLE);
        } else {
            binding.playOrderPanel.setVisibility(View.GONE);
        }
    }

    private void renderPlayOrder(TextView summary, List<Route> order) {
        String separator = getString(R.string.play_order_separator);
        int size = getResources().getDimensionPixelSize(R.dimen.play_order_portrait_size);
        SpannableStringBuilder text = new SpannableStringBuilder();
        StringBuilder description = new StringBuilder();

        for (Route route : order) {
            if (description.length() > 0) {
                text.append(separator);
                description.append(separator);
            }

            int resource = UnitPortraits.resourceFor(route.id);
            if (resource != 0) {
                Drawable portrait = getDrawable(resource);
                if (portrait != null) {
                    portrait.setBounds(0, 0, size, size);
                    int start = text.length();
                    text.append("\uFFFC\u00A0");
                    text.setSpan(new ImageSpan(portrait, ImageSpan.ALIGN_BASELINE),
                            start, start + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            String name = getString(route.nameResource);
            text.append(name);
            description.append(name);
        }
        summary.setText(text);
        summary.setContentDescription(description);
    }
}
