package com.example.xyzreader.repository.model;


import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database( entities = { Article.class, ArticleDetail.class, AppState.class},   version = 2,   exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase { /*singleton-pattern*/
    private static final String DATABASE_NAME  = "UltradianXAppDatabase";
    private static AppDatabase  sInstance;
    private static final Object LOCK  = new Object();
    public static AppDatabase   getInstance( Context context) {
        if ( sInstance == null) {  synchronized (LOCK) { sInstance = Room
                .databaseBuilder(  context.getApplicationContext(),  AppDatabase.class,  AppDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
        }}
        return sInstance;
    }
    public abstract ArticleDao itemDao();
    public abstract ArticleDetailDao itemDetailDao();
    public abstract AppStateDao appStateDao();
}
