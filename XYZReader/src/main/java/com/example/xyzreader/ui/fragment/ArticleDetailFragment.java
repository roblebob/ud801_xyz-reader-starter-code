package com.example.xyzreader.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.FragmentArticleDetailBinding;
import com.example.xyzreader.repository.viewmodel.AppViewModel;
import com.example.xyzreader.repository.viewmodel.AppViewModelFactory;
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
    //private FragmentStateAdapter mPagerAdapter;
    private List<Integer> mArticleIdList = new ArrayList<>();
    public List<Integer> getArticleIdList() { return mArticleIdList; }
    public ArticleDetailFragment() { /* Required empty public constructor */ }


    private AppViewModel mViewModel;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, appViewModelFactory).get(AppViewModel.class);

        //mPagerAdapter = new ScreenSlidePagerAdapter( this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentArticleDetailBinding.inflate( inflater, container, false);
        //mBinding.pager.setAdapter( mPagerAdapter);
        mBinding.pager.setAdapter( new ScreenSlidePagerAdapter( this));


        mViewModel.getArticleIdListLive().observe(getViewLifecycleOwner(), list -> {
            mArticleIdList = new ArrayList<>(list);
            //mPagerAdapter.notifyDataSetChanged();
            mBinding.pager.getAdapter().notifyDataSetChanged();
            mBinding.pager.setCurrentItem( ArticleDetailFragmentArgs.fromBundle( getArguments()).getPosition(), false);
        });



/*
        // moves the back arrow under the system bar
        mBinding.actionUp.setOnApplyWindowInsetsListener( (view, insets) -> {
            int statusBarSize = insets.getSystemWindowInsetTop();
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(0, statusBarSize, 0, 0);
            view.requestLayout();
            return insets;
        });

        mBinding.actionUp.setOnClickListener( (view) -> {
            ArticleDetailFragmentDirections.ActionArticleDetailFragmentToArticleListFragment action =
                    ArticleDetailFragmentDirections.actionArticleDetailFragmentToArticleListFragment();

            int pos = mBinding.pager.getCurrentItem();
            action.setId(mArticleIdList.get(pos));
            action.setPosition( pos);

            NavController navController = NavHostFragment.findNavController( this);
            navController.navigate( action);
        });
*/



        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Transition transition = TransitionInflater.from( requireContext())
                .inflateTransition( R.transition.image_shared_element_transition);

        setSharedElementEnterTransition( transition);

        int pos = ArticleDetailFragmentArgs.fromBundle( requireArguments()).getPosition();

        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements( List<String> names, Map<String, View> sharedElements) {
                        // Locate the image view at the primary fragment (the ImageFragment
                        // that is currently visible). To locate the fragment, call
                        // instantiateItem with the selection position.
                        // At this stage, the method will simply return the fragment at the
                        // position and will not create a new one.



                        Fragment currentFragment = getChildFragmentManager().findFragmentByTag("f" + pos);
                        if (currentFragment == null) {
                            Log.e(TAG, "onMapSharedElements: currentFragment is null");
                            return;
                        }
                        View view = currentFragment.getView();
                        if (view == null) {
                            Log.e(TAG, "onMapSharedElements: view is null");
                            return;
                        }

                        Log.e(TAG, names.get(0) + " " + view.findViewById(R.id.photo));

                        // Map the first shared element name to the child ImageView.
                        sharedElements.put(names.get(0), view.findViewById(R.id.photo));
                    }
                });

        postponeEnterTransition();

        //mBinding.pager.postDelayed( () -> { mBinding.pager.setCurrentItem(pos, false); }, 100);




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}