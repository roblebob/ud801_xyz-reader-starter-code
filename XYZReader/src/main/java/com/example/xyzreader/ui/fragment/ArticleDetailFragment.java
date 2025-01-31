package com.example.xyzreader.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

/** This Fragment holds the ViewPager2 that displays the individual SlidingPageFragments.
 *  It thereby replaces the old ArticleDetailActivity, not the old ArticleDetailFragment.
 *  The letter is replaced by the new SlidingPageFragment, as mentioned.
 */
public class ArticleDetailFragment extends Fragment {
    public static final String TAG = ArticleDetailFragment.class.getSimpleName();
    private FragmentArticleDetailBinding mBinding;
    private FragmentStateAdapter mPagerAdapter;
    private final List<Integer> mArticleIdList = new ArrayList<>();
    public List<Integer> getArticleIdList() { return mArticleIdList; }
    public ArticleDetailFragment() { /* Required empty public constructor */ }
    private int mPosition = RecyclerView.NO_POSITION;
    //private AppViewModel mViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = ArticleDetailFragmentArgs.fromBundle(requireArguments()).getPosition();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentArticleDetailBinding.inflate( inflater, container, false);

        AppViewModel mViewModel = new ViewModelProvider(this,
                new AppViewModelFactory(requireActivity().getApplication() )
        ).get( AppViewModel.class);

        mPagerAdapter = new ScreenSlidePagerAdapter( this);

        mBinding.pager.setAdapter( mPagerAdapter);
        mBinding.pager.registerOnPageChangeCallback( new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                mViewModel.updatePosition( position);
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

                        // Map the first shared element name, since it is the only one, to the child ImageView.
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