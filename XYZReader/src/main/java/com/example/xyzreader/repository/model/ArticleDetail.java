package com.example.xyzreader.repository.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "ArticleDetail")
public class ArticleDetail {

    @PrimaryKey                         private int id;
    @ColumnInfo(name = "body")          private final ArrayList<String> body;
    @ColumnInfo(name = "photo")         private final String photo;
    @ColumnInfo(name = "bposition")     private final int bposition;

    public ArticleDetail(int id, ArrayList<String> body, String photo, int bposition) {
        this.id = id;
        this.body = body;
        this.photo = photo;
        this.bposition = bposition;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getBody() {
        return body;
    }

    public String getPhoto() {
        return photo;
    }

    public int getBposition() {
        return bposition;
    }
}
