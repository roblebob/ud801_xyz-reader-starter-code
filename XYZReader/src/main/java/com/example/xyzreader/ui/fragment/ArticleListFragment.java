package com.example.xyzreader.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.FragmentArticleListBinding;
import com.example.xyzreader.repository.model.Article;
import com.example.xyzreader.repository.viewmodel.AppViewModel;
import com.example.xyzreader.repository.viewmodel.AppViewModelFactory;
import com.example.xyzreader.ui.adapter.ArticleListAdapter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends Fragment implements ArticleListAdapter.ItemCLickListener {
    public static final String TAG = ArticleListFragment.class.getSimpleName();
    public ArticleListFragment() { /* Required empty public constructor */ }
    FragmentArticleListBinding mBinding;
    private boolean mIsRefreshing = false;
    private AppViewModel mViewModel;

    ArticleListAdapter mArticleListAdapter;

    int mPosition = 0;
    int mId = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPosition = ArticleListFragmentArgs.fromBundle( requireArguments()).getPosition();
        mId = ArticleListFragmentArgs.fromBundle( requireArguments()).getId();
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentArticleListBinding.inflate( inflater, container, false);

        mBinding.appBarLayout .addOnOffsetChangedListener( (appBarLayout1, verticalOffset) -> {
            float alpha = ((float) (appBarLayout1.getTotalScrollRange() + verticalOffset)) / appBarLayout1.getTotalScrollRange();
            Log.e(TAG, "----> " + verticalOffset + "  " + alpha );
            appBarLayout1.setAlpha(alpha);
            mBinding.recyclerView.setAlpha( 1.0f - alpha);

            mBinding.logoIv.setScaleX( alpha);
            mBinding.logoIv.setScaleY( alpha);

            if (!mIsRefreshing && mBinding.swipeRefreshLayout.isRefreshing()) {
                updateRefreshingUI();
            }
        });

        mBinding.recyclerView .setLayoutManager( new StaggeredGridLayoutManager( getResources().getInteger( R.integer.list_column_count), StaggeredGridLayoutManager.VERTICAL));
        mArticleListAdapter = new ArticleListAdapter( getContext(), this, NavHostFragment.findNavController( this));
        mBinding.recyclerView.setAdapter(mArticleListAdapter);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory( requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this,  appViewModelFactory).get(AppViewModel.class);


        mViewModel.getAppStateByKeyLive("refreshing").observe( getViewLifecycleOwner(), value -> mIsRefreshing = value != null);

        if (savedInstanceState == null) { refresh(); }



        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        if (mPosition >= 0) {

            mBinding.appBarLayout.setExpanded( false);

            setExitSharedElementCallback( new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);

                    sharedElements.put( names.get(0),
                            Objects.requireNonNull(
                                    mBinding.recyclerView.findViewHolderForAdapterPosition(mPosition)
                            ) .itemView.findViewById( R.id.thumbnail));
                }
            });
        }

        postponeEnterTransition();

        final ViewGroup parentView = (ViewGroup) view.getParent();

        mViewModel.getArticleListLive().observe(getViewLifecycleOwner(),
                new Observer<List<Article>>() {
                    @Override
                    public void onChanged(List<Article> articles) {
                        mArticleListAdapter.submit( articles);
                        mBinding.recyclerView.scrollToPosition( mPosition);

                        parentView.getViewTreeObserver()
                                .addOnPreDrawListener(
                                        new ViewTreeObserver.OnPreDrawListener() {
                                            @Override
                                            public boolean onPreDraw() {

                                                parentView.getViewTreeObserver().removeOnPreDrawListener(this);
                                                startPostponedEnterTransition();
                                                return true;
                                            }
                                        }
                                );
                    }
                }
        );
    }

    private void refresh() {
        Log.e(TAG, "------> refresh()");
        mViewModel.refresh();
    }


    private void updateRefreshingUI() {
        Log.e(TAG, "------> updateRefreshingUI()");
        mBinding.swipeRefreshLayout .setRefreshing(mIsRefreshing);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
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











