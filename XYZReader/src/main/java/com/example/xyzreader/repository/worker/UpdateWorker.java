package com.example.xyzreader.repository.worker;

import static android.content.Context.CONNECTIVITY_SERVICE;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.repository.model.AppDatabase;
import com.example.xyzreader.repository.model.AppState;
import com.example.xyzreader.repository.model.AppStateDao;
import com.example.xyzreader.repository.model.Item;
import com.example.xyzreader.repository.model.ItemDao;
import com.example.xyzreader.repository.model.ItemDetail;
import com.example.xyzreader.repository.model.ItemDetailDao;
import com.example.xyzreader.ui.helper.ImageLoaderHelper;
import com.example.xyzreader.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateWorker extends Worker {
    public static final String TAG = UpdateWorker.class.getSimpleName();
    private final String SRC_URL;
    private static final int DEFAULT_COLOR = 0xFF333333;

    private final AppStateDao mAppStateDao;
    private final ItemDao mItemDao;
    private final ItemDetailDao mItemDetailDao;

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

        NetworkInfo networkInfo = ((ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE)) .getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            return Result.failure();
        }

        mAppStateDao.insert( new AppState("refreshing", "is refreshing"));


        try {
            Request request = new Request.Builder()
                    .url( new URL(SRC_URL ))
                    .build();

            Response response = new OkHttpClient()
                    .newCall(request)
                    .execute();


            Object object = new JSONTokener( response.body().string()) .nextValue();
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


                mItemDao.insert( new Item( id, title, author, thumb, aspectRatio, publishedDate, DEFAULT_COLOR));
                mItemDetailDao.insert( new ItemDetail( id, body, photo));


                try {
                    InputStream inputStream  = new java.net.URL(thumb).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Palette p = Palette.from(bitmap).generate();
                    int color = p.getDarkMutedColor(DEFAULT_COLOR);
                    mItemDao.updateColor( id,  color);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }



        } catch (IOException | JSONException e) {
            e.printStackTrace();
            mAppStateDao.insert( new AppState("refreshing", null));
            return Result.failure();
        }


        mAppStateDao.insert( new AppState("refreshing", null));
        return Result.success();
    }
}
