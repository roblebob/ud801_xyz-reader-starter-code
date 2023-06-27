package com.example.xyzreader.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionInflater;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends Fragment {
    public static final String TAG = ArticleListFragment.class.getSimpleName();
    public ArticleListFragment() { /* Required empty public constructor */ }
    FragmentArticleListBinding mBinding;
    private AppViewModel mViewModel;
    ArticleListAdapter mArticleListAdapter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "------> onCreate()");

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory( requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this,  appViewModelFactory).get(AppViewModel.class);

        if (savedInstanceState == null) {
            mViewModel.refresh();
            Log.d(TAG, "------> refresh()");
        }
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "------> onCreateView()");

        if (mBinding != null) return mBinding.getRoot();

        mBinding = FragmentArticleListBinding.inflate( inflater, container, false);
        mArticleListAdapter = new ArticleListAdapter(  this);
        mBinding.recyclerView .setLayoutManager( new StaggeredGridLayoutManager( getResources().getInteger( R.integer.list_column_count), StaggeredGridLayoutManager.VERTICAL));
        mBinding.recyclerView .setAdapter( mArticleListAdapter);

        setExitTransition( TransitionInflater.from( requireContext())
                .inflateTransition( R.transition.grid_exit_transition)
        );

        return mBinding.getRoot();
    }





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "------> onViewCreated()");



        NavController navController = NavHostFragment.findNavController(this);
        SavedStateHandle savedStateHandle = navController.getCurrentBackStackEntry().getSavedStateHandle();
        MutableLiveData<Integer> positionLive = savedStateHandle.getLiveData("position");
        positionLive.observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer position) {
                        // Do something with the result.
                        Log.e(TAG, "------> onChanged():  pos:" + position);
                        postponeEnterTransition();
                        mBinding.recyclerView.scrollToPosition(position);

                        setEnterSharedElementCallback(new SharedElementCallback() {
                            @Override
                            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {

                                RecyclerView.ViewHolder viewHolder = mBinding.recyclerView
                                        .findViewHolderForAdapterPosition(position);

                                if (viewHolder == null) {
                                    Log.e(TAG, "onMapSharedElements: " + "viewHolder == null");
                                    return;
                                }

                                sharedElements.put(names.get(0), viewHolder.itemView.findViewById(R.id.thumbnail));

                                Log.e(TAG, "onMapSharedElements: " + names.get(0) + "  " + viewHolder.itemView.findViewById(R.id.thumbnail));
                            }
                        });
                    }
                }
        );

        mViewModel.getAppStateByKeyLive("refreshing").observe( getViewLifecycleOwner(), value -> mBinding.swipeRefreshLayout.setRefreshing( value != null));

        final ViewGroup parentView = (ViewGroup) view.getParent();
        mViewModel.getArticleListLive().observe(getViewLifecycleOwner(),
                new Observer<List<Article>>() {
                    @Override
                    public void onChanged(List<Article> articles) {
                        Log.d(TAG, "------> onChanged()   articles.size():" + articles.size());
                        mArticleListAdapter.update( articles);

                        parentView.getViewTreeObserver()
                                .addOnPreDrawListener(
                                        new ViewTreeObserver.OnPreDrawListener() {
                                            @Override
                                            public boolean onPreDraw() {

                                                parentView.getViewTreeObserver().removeOnPreDrawListener(this);
                                                startPostponedEnterTransition();
                                                Log.d(TAG, "------> startPostponedEnterTransition()");
                                                return true;
                                            }
                                        }
                                );
                    }
                });


    }








    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "------> onViewStateRestored()");
    }


    @Override
    public void onStart() {
        super.onStart();

        mBinding.appBarLayout .addOnOffsetChangedListener( (appBarLayout1, verticalOffset) -> {

            float ratio = ((float) (appBarLayout1.getTotalScrollRange() + verticalOffset)) / appBarLayout1.getTotalScrollRange();
            //Log.d(TAG, "----> " + verticalOffset + "  " + ratio );

            appBarLayout1.setAlpha(ratio);
            mBinding.recyclerView.setAlpha( 1.0f - ratio);
            mBinding.logoIv.setScaleX( ratio);
            mBinding.logoIv.setScaleY( ratio);
        });
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }


}











