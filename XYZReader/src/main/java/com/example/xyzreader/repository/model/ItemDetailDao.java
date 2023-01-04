package com.example.xyzreader.repository.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert( ItemDetail itemDetail);

    @Query("SELECT * FROM ItemDetail WHERE :id = id")
    LiveData<ItemDetail> loadItemDetailByIdLive(int id);
}
