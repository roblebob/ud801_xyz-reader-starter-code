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

    @ColumnInfo(name = "bpos")         private int                 bpos;

    public ArticleDetail(int id, ArrayList<String> body, String photo) {
        this.id = id;
        this.body = body;
        this.photo = photo;
        this.bpos = 0;
    }


    @Ignore
    public ArticleDetail(ArticleDetail articleDetail) {
        this.id = articleDetail.id;
        this.body = articleDetail.body;
        this.photo = articleDetail.photo;
        this.bpos = articleDetail.bpos;
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

    public int getBpos() {
        return bpos;
    }
    public void setBpos(int bpos) {
        this.bpos = bpos;
    }

}
