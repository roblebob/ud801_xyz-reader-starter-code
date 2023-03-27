package com.example.xyzreader.repository.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert( Article article);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update( Article article);

    @Query(value = "UPDATE Article SET `color` = :color WHERE id = :id ")
    void updateColor(int id, int color);


    @Query("SELECT * FROM Article ORDER BY publishedDate DESC")
    LiveData<List<Article>> loadItemListLive();

    @Query("SELECT id FROM Article ORDER BY publishedDate DESC")
    LiveData<List<Integer>> loadItemIdListLive();

    @Query("SELECT * FROM Article WHERE :id = id")
    LiveData<Article> loadItemByIdLive(int id);
}
