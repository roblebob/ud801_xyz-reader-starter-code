package com.example.xyzreader.repository.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Worker;
import androidx.work.Data;
import androidx.work.WorkerParameters;

import com.example.xyzreader.repository.model.AppDatabase;
import com.example.xyzreader.repository.model.AppState;
import com.example.xyzreader.repository.model.AppStateDao;
import com.example.xyzreader.repository.model.Article;
import com.example.xyzreader.repository.model.ArticleDao;
import com.example.xyzreader.repository.model.ArticleDetailDao;

public class UpdateWorker  extends Worker {
    public static final String TAG = UpdateWorker.class.getSimpleName();
    public static final String KEY_POSITION = "position";
    public static final String KEY_BPOSITION = "bposition";
    public static final String KEY_ID = "id";
    AppStateDao mAppStateDao;
    ArticleDetailDao mArticleDetailDao;

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AppDatabase appDatabase = AppDatabase.getInstance( context);
        mAppStateDao = appDatabase.appStateDao();
        mArticleDetailDao = appDatabase.articleDetailDao();
    }

    @NonNull
    @Override
    public Result doWork() {

        Data data = getInputData();
        if (data == null) {
            return Result.failure();
        }
        if (data.hasKeyWithValueOfType(KEY_POSITION, Integer.class)) {
            int position = data.getInt(KEY_POSITION, RecyclerView.NO_POSITION);
            //mAppStateDao.updatePosition( String.valueOf(position));
            mAppStateDao.insert( new AppState( KEY_POSITION, String.valueOf(position)));
            Log.d(TAG, "doWork: position: " + position);
        }
        if (data.hasKeyWithValueOfType(KEY_BPOSITION, Integer.class) && data.hasKeyWithValueOfType(KEY_ID, Integer.class) ) {
            int bposition = data.getInt(KEY_BPOSITION, RecyclerView.NO_POSITION);
            int id = data.getInt(KEY_ID, RecyclerView.NO_POSITION);
            if (id > 0 && bposition > RecyclerView.NO_POSITION) {
                mArticleDetailDao.updateBposition(id, bposition);
            }
        }
        return Result.success();
    }
}
