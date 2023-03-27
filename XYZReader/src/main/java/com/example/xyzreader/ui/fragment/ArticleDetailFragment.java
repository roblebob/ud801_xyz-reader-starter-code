package com.example.xyzreader.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.databinding.FragmentArticleDetailBinding;
import com.example.xyzreader.repository.viewmodel.AppViewModel;
import com.example.xyzreader.repository.viewmodel.AppViewModelFactory;
import com.example.xyzreader.ui.adapter.ScreenSlidePagerAdapter;

import java.util.ArrayList;
import java.util.List;

// IMPORTANT NOTE:
//      This replaces the old ArticleDetailActivity, not the old ArticleDetailFragment.
//      The latter is replaced by the new SlidingPageFragment!!!

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleDetailFragment extends Fragment {
    public static final String TAG = ArticleDetailFragment.class.getSimpleName();


    private AppViewModel mViewModel;
    private FragmentArticleDetailBinding mBinding;
    private FragmentStateAdapter mPagerAdapter;
    private List<Integer> mArticleIdList = new ArrayList<>();
    public List<Integer> getArticleIdList() { return mArticleIdList; }

    public ArticleDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentArticleDetailBinding.inflate( inflater, container, false);


        mBinding.actionUp.setOnClickListener( (view) -> {
            ArticleDetailFragmentDirections.ActionArticleDetailFragmentToArticleListFragment action =
                    ArticleDetailFragmentDirections.actionArticleDetailFragmentToArticleListFragment();


            int pos = mBinding.pager.getCurrentItem();
            action.setId(mArticleIdList.get(pos));
            action.setPosition( pos);

            NavController navController = NavHostFragment.findNavController( this);
            navController.navigate( action);
        });


        mPagerAdapter = new ScreenSlidePagerAdapter( this);
        mBinding.pager.setAdapter( mPagerAdapter);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);

        mViewModel.getItemIdListLive().observe(getViewLifecycleOwner(), list -> {
            mArticleIdList = new ArrayList<>(list);
            mPagerAdapter.notifyDataSetChanged();
        });


        int id = ArticleDetailFragmentArgs.fromBundle( getArguments()).getId();
        int pos = ArticleDetailFragmentArgs.fromBundle( getArguments()).getPosition();
        mBinding.pager.postDelayed( () -> mBinding.pager.setCurrentItem( pos, false), 100);


        // moves the back arrow under the system bar
        mBinding.actionUp.setOnApplyWindowInsetsListener( (view, insets) -> {
            int statusBarSize = insets.getSystemWindowInsetTop();
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(0, statusBarSize, 0, 0);
            view.requestLayout();
            return insets;
        });


        return mBinding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}