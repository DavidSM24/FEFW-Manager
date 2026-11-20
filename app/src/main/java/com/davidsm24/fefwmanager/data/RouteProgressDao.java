package com.davidsm24.fefwmanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Upsert;

import com.davidsm24.fefwmanager.route.Route;

import java.util.List;

@Dao
public interface RouteProgressDao {
    @Query("SELECT * FROM main_missions")
    LiveData<List<MainMission>> observeMissions();

    @Query("""
            SELECT main_missions.day, main_missions.month, main_missions.description,
                   (SELECT COUNT(*) FROM recruited_units WHERE route_id = :route) AS unitCount,
                   (SELECT COUNT(*) FROM support_pairs WHERE route_id = :route) AS supportCount
            FROM (SELECT :route AS route_id) AS selected_route
            LEFT JOIN main_missions USING (route_id)
            """)
    LiveData<RouteProgress> observeProgress(Route route);

    @Query("SELECT unit_id FROM recruited_units WHERE route_id = :route")
    LiveData<List<String>> observeRecruitedUnitIds(Route route);

    @Upsert
    void saveMission(MainMission mission);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void recruit(RecruitedUnit unit);

    @Delete
    void removeRecruit(RecruitedUnit unit);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addSupport(SupportPair support);
}
