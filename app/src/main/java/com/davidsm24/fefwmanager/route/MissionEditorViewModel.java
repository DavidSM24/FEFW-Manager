package com.davidsm24.fefwmanager.route;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.davidsm24.fefwmanager.data.MainMission;
import com.davidsm24.fefwmanager.data.RouteProgressDao;

import java.util.concurrent.Executor;

public final class MissionEditorViewModel extends ViewModel {
    enum SaveState { IDLE, SAVING, SAVED, FAILED }

    private final RouteProgressDao progress;
    private final Executor writes;
    private final MutableLiveData<SaveState> saveState = new MutableLiveData<>(SaveState.IDLE);

    MissionEditorViewModel(RouteProgressDao progress, Executor writes) {
        this.progress = progress;
        this.writes = writes;
    }

    LiveData<SaveState> saveState() {
        return saveState;
    }

    void save(MainMission mission) {
        if (saveState.getValue() == SaveState.SAVING) {
            return;
        }
        saveState.setValue(SaveState.SAVING);
        writes.execute(() -> {
            try {
                progress.saveMission(mission);
                saveState.postValue(SaveState.SAVED);
            } catch (RuntimeException exception) {
                Log.e("MissionEditor", "Unable to save mission", exception);
                saveState.postValue(SaveState.FAILED);
            }
        });
    }

    void reset() {
        saveState.setValue(SaveState.IDLE);
    }
}
