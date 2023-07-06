package com.example.xyzreader.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.xyzreader.repository.worker.UpgradeWorker;
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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentArticleListBinding.inflate( inflater, container, false);

        AppViewModel viewModel = new ViewModelProvider(this, new AppViewModelFactory(
                requireActivity().getApplication())).get(AppViewModel.class);

        if (savedInstanceState == null) { viewModel.upgrade(); }

        ArticleListAdapter articleListAdapter = new ArticleListAdapter(  this);

        mBinding.recyclerView .setLayoutManager( new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.list_column_count), StaggeredGridLayoutManager.VERTICAL));

        mBinding.recyclerView .setAdapter( articleListAdapter);

        setExitTransition( TransitionInflater.from( requireContext())
                .inflateTransition( R.transition.grid_exit_transition) );

        viewModel.getAppStateByKeyLive( UpgradeWorker.KEY_UPGRADING).observe(
                getViewLifecycleOwner(),
                value -> mBinding.swipeRefreshLayout.setRefreshing( value != null)
        );

        viewModel.getPosition().observe( getViewLifecycleOwner(), positionString -> {
            if (positionString == null) { return; }
            int position = Integer.parseInt( positionString);
            mBinding.recyclerView.post( () ->  mBinding.recyclerView.scrollToPosition(position));
            mSetExitSharedElementCallback( position);
        });

        // <a href="https://developer.android.com/guide/fragments/animate#recyclerview">...</a>
        final ViewGroup parentView = (ViewGroup) mBinding.getRoot();
        viewModel.getArticleListLive().observe(getViewLifecycleOwner(),
                new Observer<List<Article>>() {
                    @Override
                    public void onChanged(List<Article> articles) {
                        articleListAdapter.update( articles);

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

        postponeEnterTransition();
        return mBinding.getRoot();
    }







    /**
     * when the fragment is started, we start listening to the app bar layout:
     * when the app bar is collapsed, we want to hide the app bar and show the recycler view
     * and when the app bar is expanded, we want to show the app bar and hide the recycler view
     */
    @Override
    public void onStart() {
        super.onStart();
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


    /**
     * Callback from the adapter, to navigate to the selected detail
     *
     * @param view the clicked view
     * @param position the clicked position
     */
    @Override
    public void onViewHolderClicked(View view, int position) {

        mSetExitSharedElementCallback( position);

        // Since the exit transition is fade out,
        // we need to exclude the selected clicked view from the transition
        ((TransitionSet) Objects.requireNonNull( getExitTransition()))
                .excludeTarget( view, true);

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


    /**
     * Adapting the exit shared element callback to the changing current position
     * @param position the current position
     */
    private void mSetExitSharedElementCallback(int position) {
        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {

                        // Locate the ViewHolder for the clicked position.
                        RecyclerView.ViewHolder selectedViewHolder = mBinding.recyclerView
                                .findViewHolderForAdapterPosition( position);
                        if (selectedViewHolder == null) { return; }

                        // Map the first and only shared element name to the child ImageView.
                        sharedElements.put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.thumbnail));
                    }
                });
    }
}






