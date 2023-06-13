package com.example.xyzreader.repository.worker;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.example.xyzreader.R;
import com.example.xyzreader.repository.model.AppDatabase;
import com.example.xyzreader.repository.model.AppState;
import com.example.xyzreader.repository.model.AppStateDao;
import com.example.xyzreader.repository.model.Article;
import com.example.xyzreader.repository.model.ArticleDao;
import com.example.xyzreader.repository.model.ArticleDetail;
import com.example.xyzreader.repository.model.ArticleDetailDao;
import com.example.xyzreader.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateWorker extends Worker {
    public static final String TAG = UpdateWorker.class.getSimpleName();
    private final String SRC_URL;
    private static final int DEFAULT_COLOR = 0xFF333333;

    private final AppStateDao mAppStateDao;
    private final ArticleDao mItemDao;
    private final ArticleDetailDao mItemDetailDao;

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AppDatabase appDatabase = AppDatabase.getInstance( context);
        mAppStateDao = appDatabase.appStateDao();
        mItemDao = appDatabase.itemDao();
        mItemDetailDao = appDatabase.itemDetailDao();
        SRC_URL = context.getString(R.string.src_url);
    }

    @NonNull
    @Override
    public Result doWork() {

        mAppStateDao.insert( new AppState("refreshing", "is refreshing"));

        String string;

        try {
            Request request = new Request.Builder()
                    .url(new URL(SRC_URL))
                    .build();

            Response response = new OkHttpClient()
                    .newCall(request)
                    .execute();

            string = response.body().string();

            if (string.isEmpty()) {
                throw new IOException("Empty string");
            }

        } catch (IOException e) {
            e.printStackTrace();
            mAppStateDao.insert( new AppState("refreshing", null));
            return Result.failure();
        }


        String oldChecksum = mAppStateDao.loadValueByKey("checksum");
        String newChecksum = String.valueOf(getCRC32Checksum(string));
        Log.d(TAG, string.length() + "     " + newChecksum);


        if (oldChecksum != null && oldChecksum.equals(newChecksum)) {
            mAppStateDao.insert( new AppState("refreshing", null));
            Log.d(TAG, "nothing to be updated");
            return Result.success();
        }



        try {
            Object object = new JSONTokener( string) .nextValue();

            if (!(object instanceof JSONArray)) { throw new JSONException("Expected JSONArray"); }
            JSONArray jsonArray = (JSONArray) object;


            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int id = jsonObject.getInt("id" );
                String title = jsonObject.getString("title");
                String author = jsonObject.getString("author");
                ArrayList<String> body = Util.processArticleBody( jsonObject.getString("body" ));
                String thumb = jsonObject.getString("thumb");
                String photo = jsonObject.getString("photo");
                double aspectRatio = jsonObject.getDouble("aspect_ratio");
                String publishedDate = jsonObject.getString("published_date");



                try {
                    InputStream inputStream  = new java.net.URL(thumb).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Palette p = Palette.from(bitmap).generate();
                    int color = p.getDarkMutedColor(DEFAULT_COLOR);
                    mItemDao.insert( new Article( id, title, author, thumb, aspectRatio, publishedDate, color));

                } catch (IOException e) {
                    e.printStackTrace();
                    mItemDao.insert( new Article( id, title, author, thumb, aspectRatio, publishedDate, DEFAULT_COLOR));
                }

                mItemDetailDao.insert( new ArticleDetail( id, body, photo));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            mAppStateDao.insert( new AppState("refreshing", null));
            return Result.failure();
        }


        mAppStateDao.insert( new AppState("refreshing", null));
        return Result.success();
    }


    public static long getCRC32Checksum(String string) {

        byte[] bytes = string.getBytes();
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }
}
