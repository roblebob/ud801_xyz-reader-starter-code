package com.example.xyzreader.repository.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "Article")
public class Article {

    @PrimaryKey                             private int    id;
    @ColumnInfo(name = "title")             private final String title;
    @ColumnInfo(name = "author")            private final String author;
    @ColumnInfo(name = "thumb")             private final String thumb;
    @ColumnInfo(name = "publishedDate")     private final String publishedDate;
    @ColumnInfo(name = "color")             private int    color;


    public Article(int id, String title, String author, String thumb, String publishedDate, int color) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.thumb = thumb;
        this.publishedDate = publishedDate;
        this.color = color;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getThumb() {
        return thumb;
    }
    public String getPublishedDate() {
        return publishedDate;
    }

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }
}
