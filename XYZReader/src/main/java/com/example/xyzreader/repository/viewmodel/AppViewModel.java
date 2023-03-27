package com.example.xyzreader.repository.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.xyzreader.repository.model.AppDatabase;
import com.example.xyzreader.repository.model.AppStateDao;
import com.example.xyzreader.repository.model.Article;
import com.example.xyzreader.repository.model.ArticleDao;
import com.example.xyzreader.repository.model.ArticleDetail;
import com.example.xyzreader.repository.model.ArticleDetailDao;
import com.example.xyzreader.repository.worker.UpdateWorker;

import java.util.List;

public class AppViewModel extends ViewModel {
    public static final String TAG = AppViewModel.class.getSimpleName();

    private final WorkManager mWorkManager;
    AppStateDao mAppStateDao;
    ArticleDao mItemDao;
    ArticleDetailDao mItemDetailDao;


    public AppViewModel(Application application) {
        super();
        this.mWorkManager = WorkManager.getInstance( application);
        AppDatabase appDatabase = AppDatabase.getInstance( application.getApplicationContext());
        this.mAppStateDao = appDatabase.appStateDao();
        this.mItemDao = appDatabase.itemDao();
        this.mItemDetailDao = appDatabase.itemDetailDao();
    }

    public LiveData<String> getAppStateByKeyLive(String key) { return mAppStateDao.loadValueByKeyLive( key); }

    public LiveData<List<Article>> getItemListLive() { return mItemDao.loadItemListLive(); }

    public LiveData<List<Integer>> getItemIdListLive() { return mItemDao.loadItemIdListLive(); }

    public LiveData<Article> getItemByIdLive(int id) { return mItemDao.loadItemByIdLive( id); }
    public LiveData<ArticleDetail> getItemDetailByIdLive(int id) { return mItemDetailDao.loadItemDetailByIdLive( id); }


    public void refresh() {
        mWorkManager.enqueue(OneTimeWorkRequest.from(UpdateWorker.class));
    }
}
