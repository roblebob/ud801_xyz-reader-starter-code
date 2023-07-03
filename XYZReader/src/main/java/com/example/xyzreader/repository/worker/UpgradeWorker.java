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

public class UpgradeWorker extends Worker {
    public static final String TAG = UpgradeWorker.class.getSimpleName();
    private final String SRC_URL;
    private static final int DEFAULT_COLOR = 0xFF333333;
    private final AppStateDao mAppStateDao;
    private final ArticleDao mArticleDao;
    private final ArticleDetailDao mArticleDetailDao;

    public UpgradeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AppDatabase appDatabase = AppDatabase.getInstance( context);
        mAppStateDao = appDatabase.appStateDao();
        mArticleDao = appDatabase.articleDao();
        mArticleDetailDao = appDatabase.articleDetailDao();
        SRC_URL = context.getString(R.string.src_url);
    }

    @NonNull
    @Override
    public Result doWork() {

        // telling everybody that we started
        mAppStateDao.insert( new AppState("upgrading", "is upgrading"));

        // if position is null, then we are upgrading for the first time
        if (mAppStateDao.getPosition() == null) {
            mAppStateDao.insert( new AppState("position", "0"));
        }

        // getting the data as json string from the server
        String string;
        try {
            Request request = new Request.Builder() .url(new URL(SRC_URL)) .build();
            Response response = new OkHttpClient().newCall(request).execute();
            string = response.body().string();
            response.close();
            if (string.isEmpty()) { throw new IOException("Empty string"); }
        } catch (IOException e) {
            e.printStackTrace();
            mAppStateDao.insert( new AppState("upgrading", null));  // telling everybody that we stopped
            return Result.failure();
        }

        // checking if the data (json response string) has changed by using checksums (CRC32)
        String oldChecksum = mAppStateDao.loadValueByKey("checksum");
        String newChecksum = String.valueOf(getCRC32Checksum(string));

        if (oldChecksum != null && oldChecksum.equals(newChecksum)) {
            Log.d(TAG, "nothing to be upgraded");
            mAppStateDao.insert( new AppState("upgrading", null)); // telling everybody that we stopped
            return Result.success();
        }



        // upgrading by integrating translated JSON data into the database
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
                String publishedDate = jsonObject.getString("published_date");


                // getting the color from the thumbnail image to individualise article experience
                int color = DEFAULT_COLOR;
                try {
                    InputStream inputStream  = new java.net.URL(thumb).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Palette p = Palette.from(bitmap).generate();
                    color = p.getDarkMutedColor(DEFAULT_COLOR);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // distributing the data into 2 distinct entities, combined makes up an article,
                // since the overview fragment (ArticleListFragment) does not need the detailed data
                mArticleDao.insert( new Article( id, title, author, thumb,  publishedDate, color));
                mArticleDetailDao.insert( new ArticleDetail( id, body, photo, 0));


                // mark that a response having that checksum has been processed
                mAppStateDao.insert( new AppState("checksum", newChecksum));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            mAppStateDao.insert( new AppState("upgrading", null)); // telling everybody that we stopped
            return Result.failure();
        }


        mAppStateDao.insert( new AppState("upgrading", null));  // telling everybody that we stopped
        return Result.success();
    }


    public static long getCRC32Checksum(String string) {

        byte[] bytes = string.getBytes();
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }
}
