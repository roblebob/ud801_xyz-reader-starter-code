package com.example.xyzreader.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionSet;

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
public class ArticleListFragment extends Fragment implements ArticleListAdapter.ViewHolderListener {
    public ArticleListFragment() { /* Required empty public constructor */ }
    FragmentArticleListBinding mBinding;
    private AppViewModel mViewModel;
    ArticleListAdapter mArticleListAdapter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new AppViewModelFactory(
                requireActivity().getApplication()) ).get(AppViewModel.class);

        if (savedInstanceState == null) { mViewModel.upgrade(); }
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentArticleListBinding.inflate( inflater, container, false);
        mArticleListAdapter = new ArticleListAdapter(  this);
        mBinding.recyclerView .setLayoutManager( new StaggeredGridLayoutManager( getResources().getInteger( R.integer.list_column_count), StaggeredGridLayoutManager.VERTICAL));
        mBinding.recyclerView .setAdapter( mArticleListAdapter);

        mViewModel.getAppStateByKeyLive("upgrading").observe( getViewLifecycleOwner(), value -> mBinding.swipeRefreshLayout.setRefreshing( value != null));

        setExitTransition( TransitionInflater.from( requireContext())
                .inflateTransition( R.transition.grid_exit_transition) );

        mViewModel.getPosition().observe( getViewLifecycleOwner(), positionString -> {
            if (positionString == null) {
                return;
            }
            int position = Integer.parseInt( positionString);

            mBinding.recyclerView.post( () ->  mBinding.recyclerView.scrollToPosition(position));

            // TODO
            setExitSharedElementCallback(
                    new SharedElementCallback() {
                        @Override
                        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {

                            // Locate the ViewHolder for the clicked position.
                            RecyclerView.ViewHolder selectedViewHolder = mBinding.recyclerView
                                    .findViewHolderForAdapterPosition( position);
                            if (selectedViewHolder == null) {
                                return;
                            }

                            // Map the first shared element name to the child ImageView.
                            sharedElements.put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.thumbnail));
                        }
                    });
        });

        postponeEnterTransition();

        return mBinding.getRoot();
    }





    // when the fragment's view is created, we can start observing more live data,
    // update the adapter, and start listen to the entire view hierarchy.
    // when it's ready, the recyclerview has to be ready since it is part of it,
    // and we can start the postponed transition
    // (as it is suggested by https://developer.android.com/guide/fragments/animate#recyclerview)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ViewGroup parentView = (ViewGroup) view.getParent();
        mViewModel.getArticleListLive().observe(getViewLifecycleOwner(),
                new Observer<List<Article>>() {
                    @Override
                    public void onChanged(List<Article> articles) {
                        mArticleListAdapter.update( articles);

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
                });
    }


    @Override
    public void onStart() {
        super.onStart();

        // when the app bar is collapsed, we want to hide the app bar and show the recycler view
        // and when the app bar is expanded, we want to show the app bar and hide the recycler view
        mBinding.appBarLayout .addOnOffsetChangedListener( (appBarLayout1, verticalOffset) -> {
            float ratio = ((float) (appBarLayout1.getTotalScrollRange() + verticalOffset)) / appBarLayout1.getTotalScrollRange();
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


    // Callback from the adapter, to navigate to the detail fragment
    @Override
    public void onViewHolderClicked(View view, int position) {
        // Since the exit transition is fade out, we need to exclude the clicked view from the transition
        ((TransitionSet) Objects.requireNonNull(getExitTransition())).excludeTarget(view, true);

        ArticleListFragmentDirections.ActionArticleListFragmentToArticleDetailFragment action =
                ArticleListFragmentDirections.actionArticleListFragmentToArticleDetailFragment();

        action.setPosition( position);

        View thumbnailView = view.findViewById( R.id.thumbnail);

        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement( thumbnailView, thumbnailView.getTransitionName())
                .build();

        NavController navController =  NavHostFragment.findNavController( this);

        navController.navigate( action, extras);
    }
}






