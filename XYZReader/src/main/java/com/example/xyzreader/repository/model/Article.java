package com.example.xyzreader.repository.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "Article")
public class Article {

    @PrimaryKey(autoGenerate = false)       private int    id;
    @ColumnInfo(name = "title")             private String title;
    @ColumnInfo(name = "author")            private String author;
    @ColumnInfo(name = "thumb")             private String thumb;
    @ColumnInfo(name = "aspectRatio")       private double aspectRatio;
    @ColumnInfo(name = "publishedDate")     private String publishedDate;
    @ColumnInfo(name = "color")             private int    color;


    public Article(int id, String title, String author, String thumb, double aspectRatio, String publishedDate, int color) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.thumb = thumb;
        this.aspectRatio = aspectRatio;
        this.publishedDate = publishedDate;
        this.color = color;
    }

    @Ignore
    public Article(Article item) {
        this.id = item.id;
        this.title = item.title;
        this.author = item.author;
        this.thumb = item.thumb;
        this.aspectRatio = item.aspectRatio;
        this.publishedDate = item.publishedDate;
        this.color = item.color;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article)) return false;

        Article article = (Article) o;

        if (getId() != article.getId()) return false;
        if (Double.compare(article.getAspectRatio(), getAspectRatio()) != 0) return false;
        if (getColor() != article.getColor()) return false;
        if (getTitle() != null ? !getTitle().equals(article.getTitle()) : article.getTitle() != null)
            return false;
        if (getAuthor() != null ? !getAuthor().equals(article.getAuthor()) : article.getAuthor() != null)
            return false;
        if (getThumb() != null ? !getThumb().equals(article.getThumb()) : article.getThumb() != null)
            return false;
        return getPublishedDate() != null ? getPublishedDate().equals(article.getPublishedDate()) : article.getPublishedDate() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getId();
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getAuthor() != null ? getAuthor().hashCode() : 0);
        result = 31 * result + (getThumb() != null ? getThumb().hashCode() : 0);
        temp = Double.doubleToLongBits(getAspectRatio());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getPublishedDate() != null ? getPublishedDate().hashCode() : 0);
        result = 31 * result + getColor();
        return result;
    }
}
