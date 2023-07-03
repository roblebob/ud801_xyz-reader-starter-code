package com.example.xyzreader.repository.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "ArticleDetail")
public class ArticleDetail {

    @PrimaryKey(autoGenerate = false)   private int id;
    @ColumnInfo(name = "body")          private ArrayList<String> body;
    @ColumnInfo(name = "photo")         private String photo;
    @ColumnInfo(name = "bposition")     private int bposition;

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
    public void setBody(ArrayList<String> body) {
        this.body = body;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getBposition() {
        return bposition;
    }
    public void setBposition(int bposition) {
        this.bposition = bposition;
    }

}
