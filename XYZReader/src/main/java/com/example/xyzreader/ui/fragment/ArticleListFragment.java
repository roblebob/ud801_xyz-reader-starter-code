package com.example.xyzreader.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.FragmentArticleListBinding;
import com.example.xyzreader.repository.viewmodel.AppViewModel;
import com.example.xyzreader.repository.viewmodel.AppViewModelFactory;
import com.example.xyzreader.ui.adapter.ArticleListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArticleListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleListFragment extends Fragment implements ArticleListAdapter.ItemCLickListener {
    public static final String TAG = ArticleListFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ArticleListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArticleListFragmentNew.
     */
    // TODO: Rename and change types and number of parameters
    public static ArticleListFragment newInstance(String param1, String param2) {
        ArticleListFragment fragment = new ArticleListFragment();
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

    FragmentArticleListBinding binding;
    private boolean mIsRefreshing = false;
    private AppViewModel mViewModel;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentArticleListBinding.inflate( inflater, container, false);


        binding.appBarLayout .addOnOffsetChangedListener( (appBarLayout1, verticalOffset) -> {
            float alpha = ((float) (appBarLayout1.getTotalScrollRange() + verticalOffset)) / appBarLayout1.getTotalScrollRange();
            Log.e(TAG, "----> " + verticalOffset + "  " + alpha );
            appBarLayout1.setAlpha(alpha);
            binding.recyclerView.setAlpha( 1.0f - alpha);

            binding.logoIv.setScaleX( alpha);
            binding.logoIv.setScaleY( alpha);

            if (!mIsRefreshing && binding.swipeRefreshLayout.isRefreshing()) {
                updateRefreshingUI();
            }
        });


        binding.recyclerView .setLayoutManager( new StaggeredGridLayoutManager( getResources().getInteger( R.integer.list_column_count), StaggeredGridLayoutManager.VERTICAL));

        // NEW -------------------------------------------------------------------------------------
        ArticleListAdapter articleListAdapter = new ArticleListAdapter( getContext(), this);
        binding.recyclerView.setAdapter(articleListAdapter);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory( requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);

        mViewModel.getItemListLive().observe( getViewLifecycleOwner(), articleListAdapter::submit);

        mViewModel.getAppStateByKeyLive("refreshing").observe( getViewLifecycleOwner(), value -> {
            if (value == null) {
                mIsRefreshing = false;
            } else {
                mIsRefreshing = true;
            }

        });
        // -----------------------------------------------------------------------------------------

        if (savedInstanceState == null) { refresh(); }

        // ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);

        return binding.getRoot();
    }






    private void refresh() {
        Log.e(TAG, "------> refresh()");
        mViewModel.refresh();
    }







    private void updateRefreshingUI() {
        Log.e(TAG, "------> updateRefreshingUI()");
        binding.swipeRefreshLayout .setRefreshing(mIsRefreshing);
    }







    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemCLickListener(int id, int pos) {
        ArticleListFragmentDirections.ActionArticleListFragmentToArticleDetailFragment action =
                ArticleListFragmentDirections.actionArticleListFragmentToArticleDetailFragment();

        action.setId( id);
        action.setPosition( pos);

        NavController navController = NavHostFragment.findNavController( this);

        navController.navigate( action);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


}











