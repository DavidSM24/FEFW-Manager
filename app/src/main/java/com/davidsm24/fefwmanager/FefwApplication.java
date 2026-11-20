package com.davidsm24.fefwmanager;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.davidsm24.fefwmanager.data.ProgressDatabase;
import com.davidsm24.fefwmanager.data.RouteProgressDao;
import com.davidsm24.fefwmanager.units.UnitCatalog;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class FefwApplication extends Application {
    private final Executor progressIo = Executors.newSingleThreadExecutor();
    private ProgressDatabase database;
    private LiveData<UnitCatalog.LoadResult> catalog;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(this, ProgressDatabase.class, "route-progress.db").build();
    }

    public RouteProgressDao routeProgress() {
        return database.routeProgress();
    }

    @MainThread
    public LiveData<UnitCatalog.LoadResult> unitCatalog() {
        if (catalog == null) {
            MutableLiveData<UnitCatalog.LoadResult> loaded = new MutableLiveData<>();
            catalog = loaded;
            progressIo.execute(() -> {
                try {
                    UnitCatalog units = UnitCatalog.read(
                            getResources().openRawResource(R.raw.unidades_fefw));
                    loaded.postValue(new UnitCatalog.LoadResult(units));
                } catch (IOException | JSONException | Resources.NotFoundException exception) {
                    Log.e("UnitCatalog", "Unable to load the local unit catalog", exception);
                    loaded.postValue(new UnitCatalog.LoadResult(null));
                }
            });
        }
        return catalog;
    }

    public Executor progressIo() {
        return progressIo;
    }
}
