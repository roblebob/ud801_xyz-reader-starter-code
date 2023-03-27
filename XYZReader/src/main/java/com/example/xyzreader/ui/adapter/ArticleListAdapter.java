package com.example.xyzreader.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.xyzreader.R;
import com.example.xyzreader.repository.model.Item;
import com.example.xyzreader.ui.helper.ImageLoaderHelper;
import com.google.android.material.card.MaterialCardView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {
    private final Context mContext;


    public interface ItemCLickListener { void onItemCLickListener( int id, int pos); }
    ItemCLickListener mItemCLickListener;


    public ArticleListAdapter(Context context, ItemCLickListener itemCLickListener) {
        mContext = context;
        mItemCLickListener = itemCLickListener;
        this.setHasStableIds(true);
    }

    private ArrayList<Item> mItemList = new ArrayList<>();
    public void submit(List<Item> itemList) {
        mItemList = new ArrayList<>( itemList);
        // TODO optimize
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Item item = mItemList.get( position);

        holder.cardView.setCardBackgroundColor( item.getColor());

        holder.titleView.setText( item.getTitle());

        holder.yearView.setText( String.format( Locale.getDefault(),"%d",
                LocalDateTime.ofInstant( Instant.parse( item.getPublishedDate() + "Z"), ZoneId.systemDefault()) .getYear()));

        holder.authorView.setText( item.getAuthor());

        holder.thumbnailView.setImageUrl( item.getThumb(),
                ImageLoaderHelper.getInstance( mContext).getImageLoader());

        holder.thumbnailView.setTransitionName( item.getTitle());

        holder.itemView.setOnClickListener(view1 -> mItemCLickListener.onItemCLickListener( item.getId(), position));
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

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.list_item_artical_cardview);
            thumbnailView = view.findViewById(R.id.thumbnail);
            titleView = view.findViewById(R.id.list_item_article__title_tv);
            authorView = view.findViewById(R.id.list_item_article__author_tv);
            yearView = view.findViewById(R.id.list_item_article__year_tv);
        }
    }
}
