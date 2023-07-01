package com.example.xyzreader.repository.worker;

import android.content.Context;

import androidx.annotation.NonNull;
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
        if (data.hasKeyWithValueOfType("position", Integer.class)) {
            int position = data.getInt("position", -1);
            mAppStateDao.updatePosition( String.valueOf(position));
        }
        if (data.hasKeyWithValueOfType("bposition", Integer.class) && data.hasKeyWithValueOfType("id", Integer.class) ) {
            int bposition = data.getInt("bposition", -1);
            int id = data.getInt("id", -1);
            if (id > 0 && bposition > -1) {
                mArticleDetailDao.updateBposition(id, bposition);
            }
        }
        return Result.success();
    }
}
