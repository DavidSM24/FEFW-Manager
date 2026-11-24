package com.davidsm24.fefwmanager.ui;

import android.os.Bundle;
import android.os.SystemClock;

import androidx.activity.ComponentActivity;

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
        mosaic.setAnimating(ImperialMotion.animationsEnabled(this));
    }

    @Override
    protected void onStop() {
        mosaic.setAnimating(false);
        super.onStop();
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
