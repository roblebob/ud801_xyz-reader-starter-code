package com.example.xyzreader.repository.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "ArticleDetail")
public class ArticleDetail {

    @PrimaryKey(autoGenerate = false)   private int                 id;
    @ColumnInfo(name = "body")          private ArrayList<String>   body;
    @ColumnInfo(name = "photo")         private String photo;

    public ArticleDetail(int id, ArrayList<String> body, String photo) {
        this.id = id;
        this.body = body;
        this.photo = photo;
    }

    @Ignore
    public ArticleDetail(ArticleDetail itemDetail) {
        this.id = itemDetail.id;
        this.body = itemDetail.body;
        this.photo = itemDetail.photo;
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
}
