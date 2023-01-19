package com.example.xyzreader.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.xyzreader.ui.fragment.ArticleDetailFragmentNew;
import com.example.xyzreader.ui.fragment.ScreenSlidePageFragment;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

    ArticleDetailFragmentNew mFragment;

    public ScreenSlidePagerAdapter(@NonNull ArticleDetailFragmentNew fragment) {
        super(fragment);
        mFragment = fragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ScreenSlidePageFragment.newInstance( mFragment.getArticleIdList().get(position), position);
    }

    @Override
    public int getItemCount() {
        return mFragment.getArticleIdList().size();
    }
}
