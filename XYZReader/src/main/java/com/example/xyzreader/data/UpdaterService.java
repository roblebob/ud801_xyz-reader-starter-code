package com.example.xyzreader.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.RemoteException;
import android.text.format.Time;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdaterService extends IntentService {
    private static final String TAG = "UpdaterService";
    public static final String BROADCAST_ACTION_STATE_CHANGE = "com.example.xyzreader.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING = "com.example.xyzreader.intent.extra.REFRESHING";

    Uri dirUri = ItemsContract.Items.buildDirUri();

    public UpdaterService() { super(TAG); }


    @Override
    protected void onHandleIntent(Intent intent) {
        // Don't even inspect the intent, we only do one thing, and that's fetch content.

        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            return;
        }


        //==========================================================================================
        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE)
                        .putExtra(EXTRA_REFRESHING, true)
        );
        //==========================================================================================


        try {
            Request request = new Request.Builder()
                    .url( new URL("https://go.udacity.com/xyz-reader-json" ))
                    .build();

            Response response = new OkHttpClient()
                    .newCall(request)
                    .execute();

            Object object = new JSONTokener( response.body().string())
                    .nextValue();

            if (!(object instanceof JSONArray)) {
                throw new JSONException("Expected JSONArray");
            }

            JSONArray jsonArray = (JSONArray) object;

            //-------------------------------------------------------------------------------------


            ArrayList< ContentProviderOperation> contentProviderOperations = new ArrayList<>();

            // first Operation added:  Delete all items
            contentProviderOperations .add(
                    ContentProviderOperation
                            .newDelete( dirUri)
                            .build()
            );


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                ContentValues contentValues = new ContentValues();

                contentValues.put( ItemsContract.Items.SERVER_ID,       jsonObject.getString("id" ));
                contentValues.put( ItemsContract.Items.AUTHOR,          jsonObject.getString("author" ));
                contentValues.put( ItemsContract.Items.TITLE,           jsonObject.getString("title" ));
                contentValues.put( ItemsContract.Items.BODY,            jsonObject.getString("body" ));
                contentValues.put( ItemsContract.Items.THUMB_URL,       jsonObject.getString("thumb" ));
                contentValues.put( ItemsContract.Items.PHOTO_URL,       jsonObject.getString("photo" ));
                contentValues.put( ItemsContract.Items.ASPECT_RATIO,    jsonObject.getString("aspect_ratio" ));
                contentValues.put( ItemsContract.Items.PUBLISHED_DATE,  jsonObject.getString("published_date"));

                contentProviderOperations .add(
                        ContentProviderOperation
                                .newInsert( dirUri)
                                .withValues( contentValues)
                                .build()
                );
            }


            getContentResolver()
                    .applyBatch( ItemsContract.CONTENT_AUTHORITY, contentProviderOperations);


        } catch (MalformedURLException ignored) { // TODO: throw a real error
            Log.e(TAG, "MalformedURLException (ignored == not handled)." +  " Please check your internet connection !!!");
        } catch (IOException e) {
            Log.e(TAG, "Error fetching items JSON", e);
        } catch (JSONException | RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating content.", e);
        }




        //==========================================================================================
        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE)
                        .putExtra(EXTRA_REFRESHING, false));
        //==========================================================================================
    }
}
