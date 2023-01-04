package com.example.xyzreader.repository.worker;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.repository.model.AppDatabase;
import com.example.xyzreader.repository.model.AppState;
import com.example.xyzreader.repository.model.AppStateDao;
import com.example.xyzreader.repository.model.Item;
import com.example.xyzreader.repository.model.ItemDao;
import com.example.xyzreader.repository.model.ItemDetail;
import com.example.xyzreader.repository.model.ItemDetailDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateWorker extends Worker {
    public static final String TAG = UpdateWorker.class.getSimpleName();
    private final String SRC_URL;

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
                String thumb = jsonObject.getString("thumb");
                double aspectRatio = jsonObject.getDouble("aspect_ratio");
                String publishedDate = jsonObject.getString("published_date");

                mItemDao.insert( new Item(id, title, author, thumb, aspectRatio, publishedDate));

                ArrayList<String> body = new ArrayList<>( Arrays.asList( jsonObject.getString("body" ).split("\r\n\r\n")));

                String photo = jsonObject.getString("photo");

                mItemDetailDao.insert( new ItemDetail( id, body, photo));
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
