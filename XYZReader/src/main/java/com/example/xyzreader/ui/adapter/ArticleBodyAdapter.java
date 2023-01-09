package com.example.xyzreader.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xyzreader.R;

import java.util.ArrayList;
import java.util.List;

public class ArticleBodyAdapter extends RecyclerView.Adapter<ArticleBodyAdapter.ViewHolder> {
    public static final String TAG = ArticleBodyAdapter.class.getSimpleName();

    private Context mContext;

    public ArticleBodyAdapter(Context context) {
        this.mContext = context;
        this.setHasStableIds(true);
    }


    ArrayList<String> mBodyList = new ArrayList<>();
    public void submit(List<String> bodyList) {
        mBodyList = new ArrayList<>(bodyList);
        notifyDataSetChanged();
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
    }

    @Override
    public int getItemCount() {
        return mBodyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById( R.id.list_item_body_tv);
        }
    }
}
