package com.example.xyzreader.repository.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ArticleDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert( ArticleDetail itemDetail);

    @Query("SELECT * FROM ArticleDetail WHERE :id = id")
    LiveData<ArticleDetail> loadItemDetailByIdLive(int id);

    @Query(value = "UPDATE ArticleDetail SET `bposition` = :bposition WHERE id = :id ")
    void updateBposition(int id, int bposition);

}
