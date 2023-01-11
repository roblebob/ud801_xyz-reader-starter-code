package com.example.xyzreader.repository.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert( Item item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update( Item item);

    @Query(value = "UPDATE Item SET `color` = :color WHERE id = :id ")
    void updateColor(int id, int color);


    @Query("SELECT * FROM Item ORDER BY publishedDate DESC")
    LiveData<List<Item>> loadItemListLive();

    @Query("SELECT * FROM Item WHERE :id = id")
    LiveData<Item> loadItemByIdLive(int id);
}
