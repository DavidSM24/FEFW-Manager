package com.davidsm24.fefwmanager.data;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UnitClassDao {
    @Query("SELECT * FROM unit_classes ORDER BY name COLLATE NOCASE, id")
    List<UnitClass> getAll();
}
