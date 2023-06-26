package com.example.xyzreader.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionSet;

import com.android.volley.toolbox.NetworkImageView;
import com.example.xyzreader.R;
import com.example.xyzreader.repository.model.Article;
import com.example.xyzreader.ui.fragment.ArticleListFragment;
import com.example.xyzreader.ui.fragment.ArticleListFragmentDirections;
import com.example.xyzreader.ui.helper.ImageLoaderHelper;
import com.example.xyzreader.util.Util;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;



public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {
    private final Context mContext;
    NavController mNavController;

    ArticleListFragment mArticleListFragment;

    public ArticleListAdapter(ArticleListFragment articleListFragment) {
        mArticleListFragment = articleListFragment;
        mContext = mArticleListFragment.requireContext();
        mNavController =  NavHostFragment.findNavController( mArticleListFragment);
        this.setHasStableIds(true);
    }

    final private ArrayList<Article> mItemList = new ArrayList<>();
    public void update(List<Article> itemList) {

        ListDiffCallback<Article> listDiffCallback = new ListDiffCallback<Article>( mItemList, itemList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff( listDiffCallback);
        mItemList.clear();
        mItemList.addAll( itemList);
        diffResult.dispatchUpdatesTo( this);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Article item = mItemList.get( position);

        holder.cardView.setCardBackgroundColor( item.getColor());
        holder.titleView.setText( item.getTitle());
        holder.yearView.setText( Util.extractYear( item.getPublishedDate()));
        holder.authorView.setText( item.getAuthor());
        holder.idView.setText( String.valueOf( item.getId()));
        holder.posView.setText( String.valueOf( position));
        holder.thumbnailView.setImageUrl( item.getThumb(), ImageLoaderHelper.getInstance( mContext).getImageLoader());
        holder.thumbnailView.setTransitionName( String.valueOf( item.getId()));

        holder.itemView.setOnClickListener(view1 -> {

                    ((TransitionSet) mArticleListFragment.getExitTransition())
                            .excludeTarget(view1, true);

                    com.example.xyzreader.ui.fragment.ArticleListFragmentDirections.ActionArticleListFragmentToArticleDetailFragment action =
                            ArticleListFragmentDirections.actionArticleListFragmentToArticleDetailFragment();

                    action.setId( item.getId());
                    action.setPosition( position);

                    FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                            .addSharedElement( holder.thumbnailView, holder.thumbnailView.getTransitionName())
                            .build();

                    mNavController.navigate( action, extras);
                }
        );
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }





    static class ViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView cardView;
        public NetworkImageView thumbnailView;
        public TextView titleView;
        public TextView authorView;
        public TextView yearView;

        public TextView idView;
        public TextView posView;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.list_item_article_cardview);
            thumbnailView = view.findViewById(R.id.thumbnail);
            titleView = view.findViewById(R.id.list_item_article__title_tv);
            authorView = view.findViewById(R.id.list_item_article__author_tv);
            yearView = view.findViewById(R.id.list_item_article__year_tv);
            idView = view.findViewById(R.id.list_item_article__id);
            posView = view.findViewById(R.id.list_item_article__pos);
        }
    }
}
