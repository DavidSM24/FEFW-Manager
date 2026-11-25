package com.davidsm24.fefwmanager.ui;

import android.os.Bundle;
import android.os.SystemClock;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.audio.MusicTrack;
import com.davidsm24.fefwmanager.audio.RouteMusic;
import com.davidsm24.fefwmanager.route.Route;
import com.davidsm24.fefwmanager.units.UnitDetailsDialogs;

public abstract class ImperialActivity extends ComponentActivity {
    private ImperialMosaic mosaic;
    private UnitDetailsDialogs details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mosaic = new ImperialMosaic(getResources(), SystemClock::uptimeMillis);
        findViewById(android.R.id.content).setBackground(mosaic);
    }

    @Override
    protected void onStart() {
        super.onStart();
        routeMusic().screenStarted();
        mosaic.setAnimating(ImperialMotion.animationsEnabled(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        routeMusic().switchTrack(MusicTrack.forRoute(musicRoute()));
    }

    @Override
    protected void onStop() {
        routeMusic().screenStopped();
        mosaic.setAnimating(false);
        super.onStop();
    }

    @Nullable
    protected Route musicRoute() {
        return null;
    }

    private RouteMusic routeMusic() {
        return ((FefwApplication) getApplication()).routeMusic();
    }

    protected final UnitDetailsDialogs details() {
        if (details == null) {
            details = new UnitDetailsDialogs(this);
        }
        return details;
    }

    @Override
    protected void onDestroy() {
        if (details != null) {
            details.dismiss();
        }
        super.onDestroy();
    }
}
