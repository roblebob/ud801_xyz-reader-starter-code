package com.example.xyzreader.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.FragmentArticleDetailBinding;
import com.example.xyzreader.repository.viewmodel.AppViewModel;
import com.example.xyzreader.repository.viewmodel.AppViewModelFactory;
import com.example.xyzreader.ui.adapter.ListDiffCallback;
import com.example.xyzreader.ui.adapter.ScreenSlidePagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// IMPORTANT NOTE:
//      This replaces the old ArticleDetailActivity, not the old ArticleDetailFragment.
//      The latter is replaced by the new SlidingPageFragment!!!

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleDetailFragment extends Fragment {
    public static final String TAG = ArticleDetailFragment.class.getSimpleName();
    private FragmentArticleDetailBinding mBinding;
    private FragmentStateAdapter mPagerAdapter;
    private final List<Integer> mArticleIdList = new ArrayList<>();
    public List<Integer> getArticleIdList() { return mArticleIdList; }
    public ArticleDetailFragment() { /* Required empty public constructor */ }
    private int mPosition = RecyclerView.NO_POSITION;
    private AppViewModel mViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = ArticleDetailFragmentArgs.fromBundle(requireArguments()).getPosition();
        }

        mViewModel = new ViewModelProvider(this,
                new AppViewModelFactory(requireActivity().getApplication() )
        ).get( AppViewModel.class);

        mPagerAdapter = new ScreenSlidePagerAdapter( this);

        mBinding = FragmentArticleDetailBinding.inflate( inflater, container, false);
        mBinding.pager.setAdapter( mPagerAdapter);
        mBinding.pager.registerOnPageChangeCallback( new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "onPageSelected: " + position);
                mViewModel.updatePosition( position);
                //MainActivity.mCurrentPosition = position;
                mPosition = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        mViewModel.getArticleIdListLive().observe(getViewLifecycleOwner(), list -> {

            ListDiffCallback<Integer> listDiffCallback = new ListDiffCallback<>( mArticleIdList, list);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff( listDiffCallback);
            mArticleIdList.clear();
            mArticleIdList.addAll( list);
            diffResult.dispatchUpdatesTo( mPagerAdapter);

            mBinding.pager.setCurrentItem( mPosition, false);
        });



        setSharedElementEnterTransition( TransitionInflater.from( requireContext())
                .inflateTransition( R.transition.image_shared_element_transition));

        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements( List<String> names, Map<String, View> sharedElements) {

                        Fragment currentFragment = getChildFragmentManager().findFragmentByTag("f" +
                                mPosition
                        );
                        if (currentFragment == null) {
                            Log.e(TAG, "onMapSharedElements: currentFragment is null");
                            return;
                        }
                        View view = currentFragment.getView();
                        if (view == null) {
                            Log.e(TAG, "onMapSharedElements: view is null");
                            return;
                        }

                        Log.e(TAG, names.get(0) + " of " + names.size() + "  " + view.findViewById(R.id.photo));

                        // Map the first shared element name to the child ImageView.
                        sharedElements.put(names.get(0), view.findViewById(R.id.photo));
                    }
                });

        if (savedInstanceState == null) {
            postponeEnterTransition();
        }

        return mBinding.getRoot();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}