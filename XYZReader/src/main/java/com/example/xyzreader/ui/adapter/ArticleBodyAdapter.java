package com.example.xyzreader.ui.adapter;

import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.xyzreader.R;

import java.util.ArrayList;
import java.util.List;


public class ArticleBodyAdapter extends RecyclerView.Adapter<ArticleBodyAdapter.ViewHolder> {
    public ArticleBodyAdapter() {}


    /**
     * Note, that the 0th item in the list is a blank space, for convenience.
     */
    ArrayList<String> mBodyList = new ArrayList<>();
    public void update(List<String> bodyList) {
        ArrayList<String> newBodyList = new ArrayList<>( bodyList);
        newBodyList.add( 0, " ");
        ListDiffCallback<String> listDiffCallback = new ListDiffCallback<>( mBodyList, newBodyList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff( listDiffCallback);
        mBodyList.clear();
        mBodyList.addAll( newBodyList);
        diffResult.dispatchUpdatesTo( this);
    }

    private int mColor;
    public void setColor (int color) {
        mColor = color;
    }

    private int mCurrentBposition;
    public void setCurrentBposition(int newBposition) {
        if (mCurrentBposition == newBposition) {
            return;
        }
        int oldBposition = mCurrentBposition;
        mCurrentBposition = newBposition;
        notifyItemChanged( oldBposition);
        notifyItemChanged( mCurrentBposition);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_body, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int bposition) {
        holder.textView.setText(Html.fromHtml( mBodyList.get( bposition), Html.FROM_HTML_MODE_COMPACT));
        holder.positionTv.setText( String.valueOf( bposition));

        if (mColor != 0) {
            if (bposition > 0) {
                holder.positionTv.setTextColor( mColor);
            } else {
                holder.positionTv.setTextColor(Color.TRANSPARENT);
            }
        }

        if (bposition > 0 && bposition == mCurrentBposition) {
            holder.textView.setBackgroundColor( mColor);
        }  else {
            holder.textView.setBackgroundColor( Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return mBodyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView positionTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById( R.id.list_item_body_tv);
            positionTv = itemView.findViewById( R.id.list_item_body_position_tv);
        }
    }
}
