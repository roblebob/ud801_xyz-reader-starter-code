package com.example.xyzreader.ui.fragment;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.databinding.FragmentScreenSlidePageBinding;
import com.example.xyzreader.repository.model.Article;
import com.example.xyzreader.repository.model.ArticleDetail;
import com.example.xyzreader.repository.viewmodel.AppViewModel;
import com.example.xyzreader.repository.viewmodel.AppViewModelFactory;
import com.example.xyzreader.ui.adapter.ArticleBodyAdapter;
import com.example.xyzreader.ui.helper.ImageLoaderHelper;

import java.util.List;
import java.util.Map;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScreenSlidePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenSlidePageFragment extends Fragment {
    public static final String TAG = ScreenSlidePageFragment.class.getSimpleName();
    private static final String ID = "id";
    private static final String POS = "pos";
    private int mId;
    private int mPos;
    public ScreenSlidePageFragment() { /* Required empty public constructor */ }
    private ArticleBodyAdapter mArticleBodyAdapter;
    private AppViewModel mViewModel;


    private LinearLayoutManager mLinearLayoutManager;
    private FragmentScreenSlidePageBinding mBinding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @param pos Parameter 2.
     * @return A new instance of fragment ScreenSlidePageFragment.
     */
    public static ScreenSlidePageFragment newInstance(int id, int pos) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ID, id);
        args.putInt(POS, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getInt(ID);
            mPos = getArguments().getInt(POS);
        }

        mViewModel = new ViewModelProvider(this,
                new AppViewModelFactory(requireActivity().getApplication())
        ).get(AppViewModel.class);

        mArticleBodyAdapter = new ArticleBodyAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentScreenSlidePageBinding.inflate( inflater, container, false);

        mBinding.materialToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mBinding.articleBodyRv.setAdapter( mArticleBodyAdapter);
        mLinearLayoutManager = new LinearLayoutManager( requireContext(), RecyclerView.VERTICAL, false);
        mBinding.articleBodyRv.setLayoutManager( mLinearLayoutManager);
        mBinding.materialToolbar.setNavigationOnClickListener(v -> { navigateUp(); });
        mBinding.photo.setTransitionName(String.valueOf(mPos));

        mViewModel.getArticleByIdLive( mId).observe( getViewLifecycleOwner(), article -> {
            mBinding.materialToolbar.setTitle( article.getTitle());
            mBinding.shareFab.setBackgroundTintList( ColorStateList.valueOf( article.getColor()));
            mArticleBodyAdapter.setColor( article.getColor());
        });
        mViewModel.getArticleDetailByIdLive( mId).observe( getViewLifecycleOwner(), detail -> {
            mArticleBodyAdapter.submit( detail.getBody());
            if (detail.getBposition() > 0) {
                //mBinding.appBarLayout.setExpanded(false);
                mBinding.articleBodyRv.post(() -> mBinding.articleBodyRv.scrollToPosition( detail.getBposition()));
            }

            ImageLoaderHelper
                    .getInstance(getActivity())
                    .getImageLoader()
                    .get(detail.getPhoto(), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap = imageContainer.getBitmap();
                            if (bitmap != null) {
                                mBinding.photo.setImageBitmap( imageContainer.getBitmap());
                                if (getParentFragment() != null) {
                                    getParentFragment().startPostponedEnterTransition();
                                }
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            if (getParentFragment() != null) {
                                getParentFragment().startPostponedEnterTransition();
                            }
                        }
                    });
        });

        mBinding.articleBodyRv.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    Log.e(TAG, "----->  onScrollStateChanged!!!      bpos:" + mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
                }
            }
        });

        return mBinding.getRoot();
    }




    @Override
    public void onPause() {
        super.onPause();
        mViewModel.updateBposition( mId, mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
        Log.e(TAG, "----->  onPause!!!      bpos:" + mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
    }

    private void navigateUp() {
        Log.e(TAG, "----->  onBack!!!      pos:" + mPos);
        assert this.getParentFragment() != null;
        NavController navController = NavHostFragment.findNavController( this.getParentFragment());
        navController.navigateUp();
    }

}