package com.davidsm24.fefwmanager.units;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.davidsm24.fefwmanager.data.RecruitedUnit;
import com.davidsm24.fefwmanager.data.RouteProgressDao;
import com.davidsm24.fefwmanager.data.UnitClass;
import com.davidsm24.fefwmanager.data.UnitClassDao;
import com.davidsm24.fefwmanager.route.Route;

import java.util.List;
import java.util.concurrent.Executor;

final class UnitRosterViewModel extends ViewModel {
    enum WriteState { IDLE, SAVING, FAILED }

    private final Route route;
    private final RouteProgressDao progress;
    private final Executor io;
    private final LiveData<List<RecruitedUnit>> recruited;
    private final LiveData<UnitCatalog.LoadResult> catalog;
    private final LiveData<List<UnitClass>> classes;
    private final MutableLiveData<WriteState> writes = new MutableLiveData<>(WriteState.IDLE);

    UnitRosterViewModel(Route route, RouteProgressDao progress, UnitClassDao classCatalog, Executor io,
            LiveData<UnitCatalog.LoadResult> catalog) {
        this.route = route;
        this.progress = progress;
        this.io = io;
        this.catalog = catalog;
        recruited = progress.observeRecruits(route);
        classes = classCatalog.observeAll();
    }

    LiveData<List<RecruitedUnit>> recruited() {
        return recruited;
    }

    LiveData<UnitCatalog.LoadResult> catalog() {
        return catalog;
    }

    LiveData<WriteState> writes() {
        return writes;
    }

    LiveData<List<UnitClass>> classes() {
        return classes;
    }

    void recruit(UnitLabel unit, UnitClass unitClass) {
        save(() -> progress.recruit(new RecruitedUnit(route, unit.id(), unitClass.id)));
    }

    void remove(UnitLabel unit) {
        save(() -> progress.removeRecruit(new RecruitedUnit(route, unit.id())));
    }

    private void save(Runnable operation) {
        if (writes.getValue() == WriteState.SAVING) {
            return;
        }
        writes.setValue(WriteState.SAVING);
        io.execute(() -> {
            try {
                operation.run();
                writes.postValue(WriteState.IDLE);
            } catch (RuntimeException exception) {
                Log.e("UnitRoster", "Unable to change recruited units", exception);
                writes.postValue(WriteState.FAILED);
            }
        });
    }
}
