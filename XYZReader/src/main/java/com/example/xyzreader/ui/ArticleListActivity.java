package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//import androidx.appcompat.app.ActionBarActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;

import com.example.xyzreader.R;
import com.example.xyzreader.adapter.ArticleListAdapter;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.databinding.ActivityArticleListBinding;
import com.google.android.material.appbar.AppBarLayout;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity /*ActionBarActivity*/ implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ArticleListActivity.class.toString();
    private ActivityArticleListBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityArticleListBinding.inflate( getLayoutInflater());
        setContentView( binding.getRoot());

        binding.recyclerView .setLayoutManager(
                new StaggeredGridLayoutManager(
                        getResources().getInteger( R.integer.list_column_count),
                        StaggeredGridLayoutManager.VERTICAL
                )
        );

        binding.appBarLayout .addOnOffsetChangedListener( (appBarLayout1, verticalOffset) -> {
            float alpha = ((float) (appBarLayout1.getTotalScrollRange() + verticalOffset)) / appBarLayout1.getTotalScrollRange();
            //Log.e(TAG, "----> " + verticalOffset + "  " + alpha );
            appBarLayout1.setAlpha(alpha);
            binding.recyclerView.setAlpha( 1.0f - alpha * 0.8f);

            if (!mIsRefreshing && binding.swipeRefreshLayout.isRefreshing()) {
                updateRefreshingUI();
            }
        });


        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh();
        }
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };


    private void updateRefreshingUI() {
        binding.swipeRefreshLayout .setRefreshing(mIsRefreshing);
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

}
