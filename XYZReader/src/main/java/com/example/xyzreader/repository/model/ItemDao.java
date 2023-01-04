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

    @Query("SELECT * FROM Item")
    LiveData<List<Item>> loadItemListLive();

    @Query("SELECT * FROM Item WHERE :id = id")
    LiveData<Item> loadItemByIdLive(int id);
}
