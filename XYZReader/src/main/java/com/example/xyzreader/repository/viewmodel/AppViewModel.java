package com.example.xyzreader.repository.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.xyzreader.repository.model.AppDatabase;
import com.example.xyzreader.repository.model.AppStateDao;
import com.example.xyzreader.repository.model.Article;
import com.example.xyzreader.repository.model.ArticleDao;
import com.example.xyzreader.repository.model.ArticleDetail;
import com.example.xyzreader.repository.model.ArticleDetailDao;
import com.example.xyzreader.repository.worker.UpdateWorker;
import com.example.xyzreader.repository.worker.UpgradeWorker;

import java.util.List;

public class AppViewModel extends ViewModel {
    private final WorkManager mWorkManager;
    AppStateDao mAppStateDao;
    ArticleDao mArticleDao;
    ArticleDetailDao mArticleDetailDao;


    public AppViewModel(Application application) {
        super();
        this.mWorkManager = WorkManager.getInstance( application);
        AppDatabase appDatabase = AppDatabase.getInstance( application.getApplicationContext());
        this.mAppStateDao = appDatabase.appStateDao();
        this.mArticleDao = appDatabase.articleDao();
        this.mArticleDetailDao = appDatabase.articleDetailDao();
    }


    public LiveData<String> getPosition() { return mAppStateDao.loadValueByKeyLive( "position"); }

    public LiveData<String> getAppStateByKeyLive(String key) { return mAppStateDao.loadValueByKeyLive( key); }

    public LiveData<List<Article>> getArticleListLive() { return mArticleDao.loadArticleListLive(); }
    public LiveData<List<Integer>> getArticleIdListLive() { return mArticleDao.loadArticleIdListLive(); }
    public LiveData<Article> getArticleByIdLive(int id) { return mArticleDao.loadArticleByIdLive( id); }
    public LiveData<ArticleDetail> getArticleDetailByIdLive(int id) { return mArticleDetailDao.loadItemDetailByIdLive( id); }

    public void upgrade() {
        mWorkManager.enqueue(OneTimeWorkRequest.from(UpgradeWorker.class));
    }

    public void updatePosition(int position) {

        Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.putInt( UpdateWorker.KEY_POSITION, position);
        Data data = dataBuilder.build();

        OneTimeWorkRequest.Builder requestBuilder = new OneTimeWorkRequest.Builder( UpdateWorker.class);
        requestBuilder.setInputData( data);

        mWorkManager.enqueue(requestBuilder.build());
    }


    public void updateBposition(int id, int bposition) {

        Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.putInt( UpdateWorker.KEY_ID, id);
        dataBuilder.putInt( UpdateWorker.KEY_BPOSITION, bposition);
        Data data = dataBuilder.build();

        OneTimeWorkRequest.Builder requestBuilder = new OneTimeWorkRequest.Builder( UpdateWorker.class);
        requestBuilder.setInputData( data);

        mWorkManager.enqueue(requestBuilder.build());
    }

}
