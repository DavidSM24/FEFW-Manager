package com.davidsm24.fefwmanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Upsert;

import com.davidsm24.fefwmanager.route.Route;

import java.util.List;

@Dao
public interface RouteProgressDao {
    enum SupportCreation { ADDED, DUPLICATE, MISSING_RECRUITS }

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

    @Query("SELECT * FROM support_pairs WHERE route_id = :route ORDER BY first_unit_id, second_unit_id")
    LiveData<List<SupportPair>> observeSupportPairs(Route route);

    @Upsert
    void saveMission(MainMission mission);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void recruit(RecruitedUnit unit);

    @Delete
    void removeRecruit(RecruitedUnit unit);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long addSupport(SupportPair support);

    @Delete
    void removeSupport(SupportPair support);

    @Query("""
            SELECT COUNT(*) FROM recruited_units
            WHERE route_id = :route AND unit_id IN (:firstId, :secondId)
            """)
    int countSupportRecruits(Route route, String firstId, String secondId);

    @Transaction
    default SupportCreation createSupport(SupportPair support) {
        if (countSupportRecruits(support.route, support.firstUnitId, support.secondUnitId) != 2) {
            return SupportCreation.MISSING_RECRUITS;
        }
        return addSupport(support) == -1 ? SupportCreation.DUPLICATE : SupportCreation.ADDED;
    }
}
