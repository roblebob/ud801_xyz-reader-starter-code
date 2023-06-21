package com.example.xyzreader.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.Transition;
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
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends Fragment {
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


        AppViewModelFactory appViewModelFactory = new AppViewModelFactory( requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this,  appViewModelFactory).get(AppViewModel.class);

        mPosition = ArticleListFragmentArgs.fromBundle( requireArguments()).getPosition();
        mId = ArticleListFragmentArgs.fromBundle( requireArguments()).getId();

        Log.e(TAG, "onCreate: " + "id:" + mId + "  " + "pos:" + mPosition);

    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentArticleListBinding.inflate( inflater, container, false);
        mArticleListAdapter = new ArticleListAdapter(  this);
        mBinding.recyclerView .setLayoutManager( new StaggeredGridLayoutManager( getResources().getInteger( R.integer.list_column_count), StaggeredGridLayoutManager.VERTICAL));
        mBinding.recyclerView .setAdapter( mArticleListAdapter);
        if (mId > 0) { mBinding.appBarLayout.setExpanded(false); }
        prepareTransitions();
        return mBinding.getRoot();
    }


    private void prepareTransitions() {
        TransitionInflater inflater = TransitionInflater.from( requireContext());
        Transition transition = inflater.inflateTransition( R.transition.grid_exit_transition);
        setExitTransition( transition);

//        setExitSharedElementCallback( new SharedElementCallback() {
//            @Override
//            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
//                super.onMapSharedElements(names, sharedElements);
//
//                RecyclerView.ViewHolder viewHolder = mBinding.recyclerView
//                        .findViewHolderForAdapterPosition(mPosition);
//
//                if (viewHolder == null) { return; }
//
//                sharedElements.put( names.get(0), viewHolder.itemView.findViewById( R.id.thumbnail));
//            }
//        });
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postponeEnterTransition();

        mViewModel.getAppStateByKeyLive("refreshing").observe( getViewLifecycleOwner(), value -> mIsRefreshing = value != null);

        final ViewGroup parentView = (ViewGroup) view.getParent();
        mViewModel.getArticleListLive().observe(getViewLifecycleOwner(),
                new Observer<List<Article>>() {
                    @Override
                    public void onChanged(List<Article> articles) {
                        mArticleListAdapter.submit( articles);
                        mBinding.recyclerView.scrollToPosition( mPosition );

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








    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Log.d(TAG, "------> onViewStateRestored()");

        if (savedInstanceState == null) { refresh(); }

    }


    @Override
    public void onStart() {
        super.onStart();

        mBinding.appBarLayout .addOnOffsetChangedListener( (appBarLayout1, verticalOffset) -> {

            float ratio = ((float) (appBarLayout1.getTotalScrollRange() + verticalOffset)) / appBarLayout1.getTotalScrollRange();
            Log.d(TAG, "----> " + verticalOffset + "  " + ratio );

            appBarLayout1.setAlpha(ratio);
            mBinding.recyclerView.setAlpha( 1.0f - ratio);
            mBinding.logoIv.setScaleX( ratio);
            mBinding.logoIv.setScaleY( ratio);

            if (!mIsRefreshing && mBinding.swipeRefreshLayout.isRefreshing()) {
                updateRefreshingUI();
            }
        });
    }

    private void refresh() {
        Log.d(TAG, "------> refresh()");
        mViewModel.refresh();
    }


    private void updateRefreshingUI() {
        Log.d(TAG, "------> updateRefreshingUI()");
        mBinding.swipeRefreshLayout .setRefreshing(mIsRefreshing);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }


}











