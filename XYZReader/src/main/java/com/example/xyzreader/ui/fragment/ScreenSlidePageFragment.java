package com.example.xyzreader.ui.fragment;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;

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

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, appViewModelFactory).get(AppViewModel.class);

        mArticleBodyAdapter = new ArticleBodyAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentScreenSlidePageBinding.inflate( inflater, container, false);

        mBinding.materialToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mBinding.photo.setTransitionName( String.valueOf( mId));
        mBinding.articleBodyRv.setAdapter( mArticleBodyAdapter);
        mBinding.articleBodyRv.setLayoutManager( new LinearLayoutManager( mBinding.getRoot().getContext()));

        mBinding.materialToolbar.setNavigationOnClickListener(v -> {
            Log.e(TAG, "----->  onBack!!!    id:" + mId + "  pos:" + mPos);

            mBinding.photo.setTransitionName( String.valueOf( mPos));

//            ArticleDetailFragmentDirections.ActionArticleDetailFragmentToArticleListFragment action =
//                    ArticleDetailFragmentDirections.actionArticleDetailFragmentToArticleListFragment();
//
//            action.setId(mId);
//            action.setPosition( mPos);

            assert this.getParentFragment() != null;
            NavController navController = NavHostFragment.findNavController( this.getParentFragment());

            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(mBinding.photo, mBinding.photo.getTransitionName())
                    .build();

            //navController.navigate( action, extras);


            //navController.getPreviousBackStackEntry().getSavedStateHandle().set("id", mId);
            navController.getPreviousBackStackEntry().getSavedStateHandle().set("position", mPos);
            navController.navigateUp();
        });





        mViewModel.getArticleByIdLive( mId).observe( getViewLifecycleOwner(), article -> {
            mBinding.materialToolbar.setTitle( article.getTitle());
            mBinding.shareFab.setBackgroundTintList( ColorStateList.valueOf( article.getColor()));
        });
        mViewModel.getArticleDetailByIdLive( mId).observe( getViewLifecycleOwner(), detail -> {
            mArticleBodyAdapter.submit( detail.getBody());

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





        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postponeEnterTransition();

        setExitTransition( TransitionInflater.from( requireContext()).inflateTransition( R.transition.pager_exit_transition));

    }


}