package com.example.xyzreader.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.FragmentArticleListBinding;
import com.example.xyzreader.repository.viewmodel.AppViewModel;
import com.example.xyzreader.repository.viewmodel.AppViewModelFactory;
import com.example.xyzreader.ui.adapter.ArticleListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends Fragment implements ArticleListAdapter.ItemCLickListener {
    public static final String TAG = ArticleListFragment.class.getSimpleName();
    public ArticleListFragment() { /* Required empty public constructor */ }
    FragmentArticleListBinding binding;
    private boolean mIsRefreshing = false;
    private AppViewModel mViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentArticleListBinding.inflate( inflater, container, false);

        binding.appBarLayout .addOnOffsetChangedListener( (appBarLayout1, verticalOffset) -> {
            float alpha = ((float) (appBarLayout1.getTotalScrollRange() + verticalOffset)) / appBarLayout1.getTotalScrollRange();
            Log.e(TAG, "----> " + verticalOffset + "  " + alpha );
            appBarLayout1.setAlpha(alpha);
            binding.recyclerView.setAlpha( 1.0f - alpha);

            binding.logoIv.setScaleX( alpha);
            binding.logoIv.setScaleY( alpha);

            if (!mIsRefreshing && binding.swipeRefreshLayout.isRefreshing()) {
                updateRefreshingUI();
            }
        });

        binding.recyclerView .setLayoutManager( new StaggeredGridLayoutManager( getResources().getInteger( R.integer.list_column_count), StaggeredGridLayoutManager.VERTICAL));

        // NEW -------------------------------------------------------------------------------------
        ArticleListAdapter articleListAdapter = new ArticleListAdapter( getContext(), this);
        binding.recyclerView.setAdapter(articleListAdapter);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory( requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);

        mViewModel.getItemListLive().observe( getViewLifecycleOwner(), articleListAdapter::submit);

        mViewModel.getAppStateByKeyLive("refreshing").observe( getViewLifecycleOwner(), value -> mIsRefreshing = value != null);
        // -----------------------------------------------------------------------------------------

        if (savedInstanceState == null) { refresh(); }

        // ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);

        return binding.getRoot();
    }


    private void refresh() {
        Log.e(TAG, "------> refresh()");
        mViewModel.refresh();
    }


    private void updateRefreshingUI() {
        Log.e(TAG, "------> updateRefreshingUI()");
        binding.swipeRefreshLayout .setRefreshing(mIsRefreshing);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemCLickListener(int id, int pos) {
        ArticleListFragmentDirections.ActionArticleListFragmentToArticleDetailFragment action =
                ArticleListFragmentDirections.actionArticleListFragmentToArticleDetailFragment();

        action.setId( id);
        action.setPosition( pos);

        NavController navController = NavHostFragment.findNavController( this);

        navController.navigate( action);
    }
}











