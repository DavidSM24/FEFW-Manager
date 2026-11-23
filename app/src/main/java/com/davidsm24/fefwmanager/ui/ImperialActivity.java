package com.davidsm24.fefwmanager.ui;

import android.os.Bundle;
import android.os.SystemClock;

import androidx.activity.ComponentActivity;

public abstract class ImperialActivity extends ComponentActivity {
    private ImperialMosaic mosaic;

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
}
