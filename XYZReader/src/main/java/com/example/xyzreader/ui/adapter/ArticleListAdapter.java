package com.example.xyzreader.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionSet;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.xyzreader.MainActivity;
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
    public static final String TAG = ArticleListAdapter.class.getSimpleName();
    private final Context mContext;



    public ArticleListAdapter(ArticleListFragment articleListFragment) {
        mViewHolderListener = articleListFragment;
        mContext = articleListFragment.requireContext();
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
        return new ViewHolder(view, mViewHolderListener);
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
        holder.thumbnailView.setTransitionName( String.valueOf( position));
        holder.thumbnailView.setImageUrl( item.getThumb(), ImageLoaderHelper.getInstance( mContext).getImageLoader());


//        ImageLoaderHelper
//                .getInstance(mContext.getApplicationContext())
//                .getImageLoader()
//                .get(item.getThumb(), new ImageLoader.ImageListener() {
//                    @Override
//                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
//                        Bitmap bitmap = imageContainer.getBitmap();
//                        if (bitmap != null) {
//                            holder.thumbnailView.setImageBitmap( imageContainer.getBitmap());
//                            //mArticleListFragment.startPostponedEnterTransition();
//                            //Log.e(TAG, "thumb " + position +"   (onResponse)");
//                        }
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //mArticleListFragment.startPostponedEnterTransition();
//                        //Log.e(TAG, "thumb " + position +"   (onErrorResponse)");
//                    }
//                });







    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }




    public interface ViewHolderListener {
        void onViewHolderClicked(View view, int position);
    }
    private final ViewHolderListener mViewHolderListener;




    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MaterialCardView cardView;
        public NetworkImageView thumbnailView;
        public TextView titleView;
        public TextView authorView;
        public TextView yearView;
        public TextView idView;
        public TextView posView;

        private final ViewHolderListener viewHolderListener;

        public ViewHolder(View view, ViewHolderListener viewHolderListener) {
            super(view);
            cardView = view.findViewById(R.id.list_item_article_cardview);
            thumbnailView = view.findViewById(R.id.thumbnail);
            titleView = view.findViewById(R.id.list_item_article__title_tv);
            authorView = view.findViewById(R.id.list_item_article__author_tv);
            yearView = view.findViewById(R.id.list_item_article__year_tv);
            idView = view.findViewById(R.id.list_item_article__id);
            posView = view.findViewById(R.id.list_item_article__pos);
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
