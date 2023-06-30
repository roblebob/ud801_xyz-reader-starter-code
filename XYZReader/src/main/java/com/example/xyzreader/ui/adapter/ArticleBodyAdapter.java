package com.example.xyzreader.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.xyzreader.R;
import com.example.xyzreader.repository.model.Article;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;


public class ArticleBodyAdapter extends RecyclerView.Adapter<ArticleBodyAdapter.ViewHolder> {
    public ArticleBodyAdapter() {}


    ArrayList<String> mBodyList = new ArrayList<>();
    public void submit(List<String> bodyList) {
        ListDiffCallback<String> listDiffCallback = new ListDiffCallback<>( mBodyList, bodyList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff( listDiffCallback);
        mBodyList.clear();
        mBodyList.addAll( bodyList);
        diffResult.dispatchUpdatesTo( this);
    }

    private int mColor;
    public void setColor (int color) {
        mColor = color;
        //notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_body, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText( mBodyList.get( position));
        holder.positionTv.setText( String.valueOf( position));
        if (mColor != 0) {
            holder.cardView.setCardBackgroundColor( mColor);
            holder.cardView.setAlpha( 0.5f);
        }

    }

    @Override
    public int getItemCount() {
        return mBodyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView positionTv;
        public MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById( R.id.list_item_body_tv);
            positionTv = itemView.findViewById( R.id.list_item_body_position_tv);
            cardView = itemView.findViewById( R.id.list_item_body_cardview);
        }
    }
}
