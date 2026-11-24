package com.davidsm24.fefwmanager.data;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UnitClassDao {
    @Query("SELECT * FROM unit_classes ORDER BY name COLLATE NOCASE, id")
    LiveData<List<UnitClass>> observeAll();

    @Query("SELECT * FROM unit_classes ORDER BY name COLLATE NOCASE, id")
    List<UnitClass> getAll();

    @Nullable
    @Query("SELECT * FROM unit_classes WHERE id = :id")
    UnitClass findById(String id);
}
