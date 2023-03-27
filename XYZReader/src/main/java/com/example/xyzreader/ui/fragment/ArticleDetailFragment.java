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
 * Use the {@link ArticleDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleDetailFragment extends Fragment {
    public static final String TAG = ArticleDetailFragment.class.getSimpleName();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppViewModel mViewModel;
    private FragmentArticleDetailBinding mBinding;
    private FragmentStateAdapter mPagerAdapter;
    private List<Integer> mArticleIdList = new ArrayList<>();
    public List<Integer> getArticleIdList() { return mArticleIdList; }

    public ArticleDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArticleDetailFragmentNew.
     */
    // TODO: Rename and change types and number of parameters
    public static ArticleDetailFragment newInstance(String param1, String param2) {
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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