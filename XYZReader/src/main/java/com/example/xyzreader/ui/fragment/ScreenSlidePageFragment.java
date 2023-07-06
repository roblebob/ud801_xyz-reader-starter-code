package com.example.xyzreader.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.databinding.FragmentScreenSlidePageBinding;
import com.example.xyzreader.repository.viewmodel.AppViewModel;
import com.example.xyzreader.repository.viewmodel.AppViewModelFactory;
import com.example.xyzreader.ui.adapter.ArticleBodyAdapter;
import com.example.xyzreader.ui.helper.ImageLoaderHelper;

import java.util.ArrayList;
import java.util.List;

/** This Fragment is the template of the ViewPager2 within the ArticleDetailFragment.
 *  It thereby replaces the old ArticleDetailFragment.
 *  Whereas the 'id' and 'pos' are passed as arguments to this Fragment, referencing the article
 */
public class ScreenSlidePageFragment extends Fragment {
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentScreenSlidePageBinding.inflate( inflater, container, false);

        mViewModel = new ViewModelProvider(this,
                new AppViewModelFactory(requireActivity().getApplication())
        ).get(AppViewModel.class);

        mArticleBodyAdapter = new ArticleBodyAdapter();

        mBinding.articleBodyRv.setAdapter( mArticleBodyAdapter);
        mLinearLayoutManager = new LinearLayoutManager( requireContext(), RecyclerView.VERTICAL, false);
        mBinding.articleBodyRv.setLayoutManager( mLinearLayoutManager);

        mBinding.materialToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mBinding.materialToolbar.setNavigationOnClickListener(v -> {
            assert this.getParentFragment() != null;
            NavController navController = NavHostFragment.findNavController( this.getParentFragment());
            navController.navigateUp();
        });

        mBinding.photo.setTransitionName(String.valueOf(mPos));


        mViewModel.getArticleByIdLive( mId).observe( getViewLifecycleOwner(), article -> {
            mBinding.materialToolbar.setTitle( article.getTitle());
            mBinding.shareFab.setBackgroundTintList( ColorStateList.valueOf( article.getColor()));
            mArticleBodyAdapter.setColor( article.getColor());
        });
        mViewModel.getArticleDetailByIdLive( mId).observe( getViewLifecycleOwner(), detail -> {
            mArticleBodyAdapter.update( detail.getBody());
            mBinding.articleBodyRv.post(() -> mBinding.articleBodyRv.scrollToPosition( detail.getBposition()));
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


        /*
         * highlights the current paragraph
         */
        mBinding.articleBodyRv.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mArticleBodyAdapter.setCurrentBposition( findBposition());
            }
        });


        /*
         * copies the current highlighted paragraph to the clipboard, when pressed
         */
        mBinding.shareFab.setOnClickListener(v -> {
            int currentBpos = findBposition();
            RecyclerView.ViewHolder selectedViewHolder = mBinding.articleBodyRv.findViewHolderForAdapterPosition( currentBpos);
            if (selectedViewHolder == null) { return; }
            if (selectedViewHolder instanceof ArticleBodyAdapter.ViewHolder) {
                String clipboardText = ((ArticleBodyAdapter.ViewHolder) selectedViewHolder).textView.getText().toString();
                this.getContext();
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService( Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(String.valueOf( currentBpos), clipboardText);
                clipboard.setPrimaryClip(clip);
            }
        });

        return mBinding.getRoot();
    }


    /**
     * Find the current bposition (body position) of the first completely visible item of the
     * ArticleBodyAdapter (current paragraph)
     * Note, the first item 0 is blank, we start with 1
     * @return int
     */
    public int findBposition() {

        int bposition = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();

        if (bposition > RecyclerView.NO_POSITION) {
            return Math.max( 1, bposition);
        }

        List<Integer> list = new ArrayList<>();
        //list.add( mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
        list.add( mLinearLayoutManager.findFirstVisibleItemPosition());
        list.add( mLinearLayoutManager.findLastCompletelyVisibleItemPosition());
        list.add( mLinearLayoutManager.findLastVisibleItemPosition());
        list.removeIf( i -> i == RecyclerView.NO_POSITION);
        int res = (int) list.stream().mapToDouble(i -> i).average().orElse(1.0);
        return Math.max( 1, res);
    }





    /**
     * Save scroll position
     */
    @Override
    public void onPause() {
        super.onPause();
        mViewModel.updateBposition( mId, findBposition());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}