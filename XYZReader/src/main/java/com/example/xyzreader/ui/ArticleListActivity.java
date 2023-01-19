package com.example.xyzreader.ui;

import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.repository.viewmodel.AppViewModel;
import com.example.xyzreader.repository.viewmodel.AppViewModelFactory;
import com.example.xyzreader.ui.adapter.ArticleListAdapter;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.databinding.ActivityArticleListBinding;
import com.example.xyzreader.ui.adapter.ArticleListAdapterNew;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ArticleListActivity.class.toString();
    private ActivityArticleListBinding binding;
    private AppViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getLoaderManager().initLoader(0, null, this);

        binding = ActivityArticleListBinding.inflate( getLayoutInflater());
        setContentView( binding.getRoot());


        binding.appBarLayout .addOnOffsetChangedListener( (appBarLayout1, verticalOffset) -> {
            float alpha = ((float) (appBarLayout1.getTotalScrollRange() + verticalOffset)) / appBarLayout1.getTotalScrollRange();
            Log.e(TAG, "----> " + verticalOffset + "  " + alpha );
            appBarLayout1.setAlpha(alpha);
            binding.recyclerView.setAlpha( 1.0f - alpha);

            if (!mIsRefreshing && binding.swipeRefreshLayout.isRefreshing()) {
                updateRefreshingUI();
            }
        });


        binding.recyclerView .setLayoutManager( new StaggeredGridLayoutManager( getResources().getInteger( R.integer.list_column_count), StaggeredGridLayoutManager.VERTICAL));

        // NEW -------------------------------------------------------------------------------------
        //ArticleListAdapterNew articleListAdapterNew = new ArticleListAdapterNew( this, this);
        //binding.recyclerView.setAdapter( articleListAdapterNew);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(this.getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);

        //mViewModel.getItemListLive().observe(this, articleListAdapterNew::submit);

        mViewModel.getAppStateByKeyLive("refreshing").observe( this, value -> {
            if (value == null) {
                mIsRefreshing = false;
            } else {
                mIsRefreshing = true;
            }
        });
        // -----------------------------------------------------------------------------------------






        if (savedInstanceState == null) { refresh(); }
        setSupportActionBar(binding.toolbar);
    }





    private void refresh() {
        Log.e(TAG, "------> refresh()");
        startService(new Intent(this, UpdaterService.class));
        mViewModel.refresh();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }


    private boolean mIsRefreshing = false;

    private final BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals( intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };


    private void updateRefreshingUI() {
        Log.e(TAG, "------> updateRefreshingUI()");
        binding.swipeRefreshLayout .setRefreshing(mIsRefreshing);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.e(TAG, "------> onCreateLoader()");
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.e(TAG, "------> onLoadFinished()");
        binding.recyclerView.setAdapter( new ArticleListAdapter(cursor, this));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(TAG, "------> onLoaderReset()");
        binding.recyclerView.setAdapter(null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        Log.e(TAG, "------> onCreateOptionsMenu(...)");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.e(TAG, "------> onOptionsItemSelected(...)");
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // just a test, or better even before that

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        //postponeEnterTransition();
    }


}
