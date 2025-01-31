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

    @Query("SELECT * FROM Article ORDER BY publishedDate DESC")
    LiveData<List<Article>> loadArticleListLive();

    @Query("SELECT id FROM Article ORDER BY publishedDate DESC")
    LiveData<List<Integer>> loadArticleIdListLive();

    @Query("SELECT * FROM Article WHERE :id = id")
    LiveData<Article> loadArticleByIdLive(int id);
}
