package com.example.xyzreader.repository.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AppStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppState appState);

    @Query(value = "SELECT value FROM AppState WHERE `key` = 'position'")
    String getPosition();

    @Query("SELECT value FROM AppState WHERE :key = `key`")
    LiveData<String> loadValueByKeyLive(String key);

    @Query("SELECT value FROM AppState WHERE :key = `key`")
    String loadValueByKey( String key);
}
