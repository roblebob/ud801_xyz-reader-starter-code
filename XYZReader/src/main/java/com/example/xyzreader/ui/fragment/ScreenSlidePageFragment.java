package com.example.xyzreader.ui.fragment;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.databinding.FragmentScreenSlidePageBinding;
import com.example.xyzreader.repository.model.Item;
import com.example.xyzreader.repository.model.ItemDetail;
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
    private static final String ID = "id";
    private static final String POS = "pos";
    private int mId;
    private int mPos;
    public ScreenSlidePageFragment() {
        // Required empty public constructor
    }
    private FragmentScreenSlidePageBinding mBinding;
    private AppViewModel mViewModel;
    private Item mItem;
    private ItemDetail mItemDetail;
    private ArticleBodyAdapter mArticleBodyAdapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentScreenSlidePageBinding.inflate( inflater, container, false);

        ArticleBodyAdapter articleBodyAdapter = new ArticleBodyAdapter();
        mBinding.articleBodyRv.setAdapter( articleBodyAdapter);
        mBinding.articleBodyRv.setLayoutManager( new LinearLayoutManager( mBinding.getRoot().getContext()));


        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);
        mViewModel.getItemByIdLive( mId).observe( getViewLifecycleOwner(), it -> {
            Item item = new Item( it);

        });
        mViewModel.getItemDetailByIdLive( mId).observe( getViewLifecycleOwner(), detail -> {
            articleBodyAdapter.submit( detail.getBody());

            ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
                    .get(detail.getPhoto(), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap = imageContainer.getBitmap();
                            if (bitmap != null) {
                                // Palette p = Palette.generate(bitmap, 12);
                                Palette p = Palette.from(bitmap).generate();
                                int mMutedColor = p.getDarkMutedColor(0xFF333333);
                                mBinding.photo.setImageBitmap( imageContainer.getBitmap());
                                mBinding.materialToolbar .setBackgroundColor(mMutedColor);
                                mBinding.shareFab.setBackgroundTintList(ColorStateList.valueOf( mMutedColor));
                                //updateStatusBar();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });
        });





        return mBinding.getRoot();
    }



}