package com.example.xyzreader.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.xyzreader.R;
import com.example.xyzreader.repository.model.Article;
import com.example.xyzreader.ui.fragment.ArticleListFragment;
import com.example.xyzreader.ui.helper.ImageLoaderHelper;
import com.example.xyzreader.util.Util;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;



public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {
    public static final String TAG = ArticleListAdapter.class.getSimpleName();
    private final Context mContext;
    private final ArrayList<Article> mArticleList = new ArrayList<>();
    private final ViewHolderListener mViewHolderListener;

    public ArticleListAdapter(ArticleListFragment articleListFragment) {
        mViewHolderListener = articleListFragment;
        mContext = articleListFragment.requireContext();
    }

    public void update(List<Article> itemList) {

        ListDiffCallback<Article> listDiffCallback = new ListDiffCallback<>(mArticleList, itemList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff( listDiffCallback);
        mArticleList.clear();
        mArticleList.addAll( itemList);
        diffResult.dispatchUpdatesTo( this);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(view, mViewHolderListener);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article =  mArticleList.get( position);

        holder.cardView.setCardBackgroundColor( article.getColor());
        holder.titleView.setText( article.getTitle());
        holder.yearView.setText( Util.extractYear( article.getPublishedDate()));
        holder.authorView.setText( article.getAuthor());
        holder.thumbnailView.setTransitionName( String.valueOf( position));
        holder.thumbnailView.setImageUrl( article.getThumb(), ImageLoaderHelper.getInstance( mContext.getApplicationContext()).getImageLoader());
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }




    public interface ViewHolderListener {
        void onViewHolderClicked(View view, int position);
    }





    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MaterialCardView cardView;
        public NetworkImageView thumbnailView;
        public TextView titleView;
        public TextView authorView;
        public TextView yearView;

        private final ViewHolderListener viewHolderListener;

        public ViewHolder(View view, ViewHolderListener viewHolderListener) {
            super(view);
            cardView = view.findViewById(R.id.list_item_article_cardview);
            thumbnailView = view.findViewById(R.id.thumbnail);
            titleView = view.findViewById(R.id.list_item_article__title_tv);
            authorView = view.findViewById(R.id.list_item_article__author_tv);
            yearView = view.findViewById(R.id.list_item_article__year_tv);
            this.viewHolderListener = viewHolderListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Element " + getBindingAdapterPosition() + " clicked.");
            viewHolderListener.onViewHolderClicked(v, getBindingAdapterPosition());
        }
    }
}


