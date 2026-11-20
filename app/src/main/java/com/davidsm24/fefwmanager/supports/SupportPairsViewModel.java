package com.davidsm24.fefwmanager.supports;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.davidsm24.fefwmanager.data.RouteProgressDao;
import com.davidsm24.fefwmanager.data.SupportPair;
import com.davidsm24.fefwmanager.route.Route;
import com.davidsm24.fefwmanager.units.UnitLabel;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

final class SupportPairsViewModel extends ViewModel {
    enum WriteState { IDLE, SAVING, DUPLICATE, MISSING_RECRUITS, FAILED }

    private final Route route;
    private final RouteProgressDao progress;
    private final Executor io;
    private final LiveData<List<SupportPair>> pairs;
    private final LiveData<List<String>> recruits;
    private final MutableLiveData<WriteState> writes = new MutableLiveData<>(WriteState.IDLE);

    SupportPairsViewModel(Route route, RouteProgressDao progress, Executor io) {
        this.route = route;
        this.progress = progress;
        this.io = io;
        pairs = progress.observeSupportPairs(route);
        recruits = progress.observeRecruitedUnitIds(route);
    }

    LiveData<List<SupportPair>> pairs() {
        return pairs;
    }

    LiveData<List<String>> recruits() {
        return recruits;
    }

    LiveData<WriteState> writes() {
        return writes;
    }

    void create(UnitLabel first, UnitLabel second) {
        write(() -> switch (progress.createSupport(new SupportPair(route, first.id(), second.id()))) {
            case ADDED -> WriteState.IDLE;
            case DUPLICATE -> WriteState.DUPLICATE;
            case MISSING_RECRUITS -> WriteState.MISSING_RECRUITS;
        });
    }

    void remove(SupportPair pair) {
        write(() -> {
            progress.removeSupport(pair);
            return WriteState.IDLE;
        });
    }

    private void write(Supplier<WriteState> change) {
        if (writes.getValue() == WriteState.SAVING) {
            return;
        }
        writes.setValue(WriteState.SAVING);
        io.execute(() -> {
            try {
                writes.postValue(change.get());
            } catch (RuntimeException exception) {
                Log.e("SupportPairs", "Unable to change support pairs", exception);
                writes.postValue(WriteState.FAILED);
            }
        });
    }
}
