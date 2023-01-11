package com.example.xyzreader.repository.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "Item")
public class Item {

    @PrimaryKey(autoGenerate = false)       private int    id;
    @ColumnInfo(name = "title")             private String title;
    @ColumnInfo(name = "author")            private String author;
    @ColumnInfo(name = "thumb")             private String thumb;
    @ColumnInfo(name = "aspectRatio")       private double aspectRatio;
    @ColumnInfo(name = "publishedDate")     private String publishedDate;
    @ColumnInfo(name = "color")             private int    color;


    public Item(int id, String title, String author, String thumb, double aspectRatio, String publishedDate, int color) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.thumb = thumb;
        this.aspectRatio = aspectRatio;
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
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getThumb() {
        return thumb;
    }
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }
    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getPublishedDate() {
        return publishedDate;
    }
    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }
}
