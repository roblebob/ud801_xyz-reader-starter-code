package com.example.xyzreader.repository.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "ItemDetail")
public class ItemDetail {

    @PrimaryKey(autoGenerate = false)   private int                 id;
    @ColumnInfo(name = "body")          private ArrayList<String>   body;
    @ColumnInfo(name = "photo")         private String photo;

    public ItemDetail(int id, ArrayList<String> body, String photo) {
        this.id = id;
        this.body = body;
        this.photo = photo;
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
