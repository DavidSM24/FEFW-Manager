package com.davidsm24.fefwmanager.home;

import android.app.Activity;
import android.os.Bundle;

import com.davidsm24.fefwmanager.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
