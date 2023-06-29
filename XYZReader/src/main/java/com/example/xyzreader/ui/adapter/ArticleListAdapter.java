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
    }

    private ArrayList<Article> mItemList = new ArrayList<>();
    public void update(List<Article> itemList) {

        ListDiffCallback<Article> listDiffCallback = new ListDiffCallback<>( mItemList, itemList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff( listDiffCallback);
        mItemList.clear();
        mItemList.addAll( itemList);
        diffResult.dispatchUpdatesTo( this);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(view, mViewHolderListener, mContext);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Article item = mItemList.get( position);
        holder.bind( item);

        //holder.thumbnailView.setImageUrl( item.getThumb(), ImageLoaderHelper.getInstance( mContext).getImageLoader());
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
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView authorView;
        public TextView yearView;
        public TextView idView;
        public TextView posView;

        private final ViewHolderListener viewHolderListener;

        private final Context context;

        public ViewHolder(View view, ViewHolderListener viewHolderListener, Context context) {
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
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Element " + getBindingAdapterPosition() + " clicked.");
            viewHolderListener.onViewHolderClicked(v, getBindingAdapterPosition());
        }

        public void bind(Article article) {
            cardView.setCardBackgroundColor( article.getColor());
            titleView.setText( article.getTitle());
            yearView.setText( Util.extractYear( article.getPublishedDate()));
            authorView.setText( article.getAuthor());
            idView.setText( String.valueOf( article.getId()));
            posView.setText( String.valueOf( getBindingAdapterPosition()));
            thumbnailView.setTransitionName( String.valueOf( getBindingAdapterPosition()));

            ImageLoaderHelper
                    .getInstance(context.getApplicationContext())
                    .getImageLoader()
                    .get(article.getThumb(), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap = imageContainer.getBitmap();
                            if (bitmap != null) {
                                thumbnailView.setImageBitmap( imageContainer.getBitmap());
                                Log.e(TAG, "thumb " + getBindingAdapterPosition() +"   (onResponse)");
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e(TAG, "thumb " + getBindingAdapterPosition() +"   (onErrorResponse)");
                        }
                    });
        }
    }
}


